package dev.scoreboard.infrastructure.adapters.outbound;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamName;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * All games are kept in an immutable snapshot.
 * A write builds a new snapshot under the write lock and replaces the current one.
 * A read takes the current snapshot and never waits for a write.
 */
public class InMemoryGameRepository implements GameRepository {
    private record StoredGame(
        GameId id,
        TeamPair teams,
        Score score,
        int scoreRevision
    ) {}

    // The current implementation requires the maps to be unmodifiable.
    private record Snapshot(
        Map<GameId, StoredGame> storedGames,
        Map<TeamName, GameId> gameIdsByTeamName
    ) {}

    private static final Snapshot EMPTY_SNAPSHOT = new Snapshot(Map.of(), Map.of());

    private final Object writeLock = new Object();
    private volatile Snapshot snapshot = EMPTY_SNAPSHOT;
    private long nextId = 1;

    @Override
    public Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException {
        TeamPair teams = game.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();
        Score score = game.score();
        int scoreRevision = game.scoreRevision();

        synchronized (this.writeLock) {
            Snapshot currentSnapshot = this.snapshot;
            ensureTeamIsNotPlaying(currentSnapshot, homeTeam);
            ensureTeamIsNotPlaying(currentSnapshot, awayTeam);

            GameId id = new GameId(this.nextId);
            StoredGame storedGame = new StoredGame(id, teams, score, scoreRevision);
            Snapshot newSnapshot = createSnapshotWithAddedGame(currentSnapshot, storedGame);

            this.nextId++;
            this.snapshot = newSnapshot;

            return createGame(storedGame);
        }
    }

    private static void ensureTeamIsNotPlaying(Snapshot snapshot, TeamName teamName)
            throws TeamsNotUniqueException {
        GameId idOfGameWithTeam = snapshot.gameIdsByTeamName().get(teamName);
        if (idOfGameWithTeam == null) {
            return;
        }

        StoredGame storedGameWithTeam = snapshot.storedGames().get(idOfGameWithTeam);
        Game gameWithTeam = createGame(storedGameWithTeam);
        throw new TeamsNotUniqueException(gameWithTeam);
    }

    @Override
    public void updateScoreIfNextRevision(GameScore gameScore)
            throws GameMissingException, ScoreRevisionConflictException {
        GameId gameId = gameScore.gameId();
        Score newScore = gameScore.score();
        int newScoreRevision = gameScore.scoreRevision();

        synchronized (this.writeLock) {
            Snapshot currentSnapshot = this.snapshot;
            StoredGame storedGame = currentSnapshot.storedGames().get(gameId);
            if (storedGame == null) {
                throw new GameMissingException(gameId);
            }

            int storedScoreRevision = storedGame.scoreRevision();
            boolean revisionIsNext = newScoreRevision - 1 == storedScoreRevision;
            if (!revisionIsNext) {
                Game currentGame = createGame(storedGame);
                throw new ScoreRevisionConflictException(currentGame);
            }

            TeamPair teams = storedGame.teams();
            StoredGame updatedGame = new StoredGame(gameId, teams, newScore, newScoreRevision);
            Snapshot newSnapshot = createSnapshotWithUpdatedGame(currentSnapshot, updatedGame);

            this.snapshot = newSnapshot;
        }
    }

    @Override
    public void removeGame(GameId id) throws GameMissingException {
        synchronized (this.writeLock) {
            Snapshot currentSnapshot = this.snapshot;
            StoredGame storedGame = currentSnapshot.storedGames().get(id);
            if (storedGame == null) {
                throw new GameMissingException(id);
            }

            Snapshot newSnapshot = createSnapshotWithoutGame(currentSnapshot, storedGame);

            this.snapshot = newSnapshot;
        }
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        Snapshot currentSnapshot = this.snapshot;
        StoredGame storedGame = currentSnapshot.storedGames().get(id);
        if (storedGame == null) {
            return Optional.empty();
        }

        Game game = createGame(storedGame);
        return Optional.of(game);
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        Snapshot currentSnapshot = this.snapshot;
        TeamName homeTeam = teams.homeTeam();
        GameId idOfGameWithHomeTeam = currentSnapshot.gameIdsByTeamName().get(homeTeam);
        if (idOfGameWithHomeTeam == null) {
            return Optional.empty();
        }

        StoredGame storedGameWithHomeTeam = currentSnapshot.storedGames().get(idOfGameWithHomeTeam);
        TeamPair teamsOfGame = storedGameWithHomeTeam.teams();
        boolean found = teams.equals(teamsOfGame);
        if (!found) {
            return Optional.empty();
        }

        Game game = createGame(storedGameWithHomeTeam);
        return Optional.of(game);
    }

    @Override
    public List<Game> findAllGames() {
        Snapshot currentSnapshot = this.snapshot;
        Collection<StoredGame> allStoredGames = currentSnapshot.storedGames().values();

        List<Game> games = new ArrayList<>();
        for (StoredGame storedGame : allStoredGames) {
            Game game = createGame(storedGame);
            games.add(game);
        }

        return games;
    }

    private static Snapshot createSnapshotWithAddedGame(Snapshot snapshot, StoredGame addedGame) {
        Map<GameId, StoredGame> storedGames = new HashMap<>(snapshot.storedGames());
        Map<TeamName, GameId> gameIdsByTeamName = new HashMap<>(snapshot.gameIdsByTeamName());

        GameId id = addedGame.id();
        TeamPair teams = addedGame.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();

        storedGames.put(id, addedGame);
        gameIdsByTeamName.put(homeTeam, id);
        gameIdsByTeamName.put(awayTeam, id);

        Map<GameId, StoredGame> unmodifiableStoredGames = Collections.unmodifiableMap(storedGames);
        Map<TeamName, GameId> unmodifiableGameIdsByTeamName = Collections.unmodifiableMap(gameIdsByTeamName);
        return new Snapshot(unmodifiableStoredGames, unmodifiableGameIdsByTeamName);
    }

    private static Snapshot createSnapshotWithUpdatedGame(Snapshot snapshot, StoredGame updatedGame) {
        Map<GameId, StoredGame> storedGames = new HashMap<>(snapshot.storedGames());
        Map<TeamName, GameId> unmodifiableGameIdsByTeamName = snapshot.gameIdsByTeamName();

        GameId id = updatedGame.id();
        storedGames.put(id, updatedGame);

        Map<GameId, StoredGame> unmodifiableStoredGames = Collections.unmodifiableMap(storedGames);
        return new Snapshot(unmodifiableStoredGames, unmodifiableGameIdsByTeamName);
    }

    private static Snapshot createSnapshotWithoutGame(Snapshot snapshot, StoredGame removedGame) {
        Map<GameId, StoredGame> storedGames = new HashMap<>(snapshot.storedGames());
        Map<TeamName, GameId> gameIdsByTeamName = new HashMap<>(snapshot.gameIdsByTeamName());

        GameId id = removedGame.id();
        TeamPair teams = removedGame.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();

        storedGames.remove(id);
        gameIdsByTeamName.remove(homeTeam);
        gameIdsByTeamName.remove(awayTeam);

        Map<GameId, StoredGame> unmodifiableStoredGames = Collections.unmodifiableMap(storedGames);
        Map<TeamName, GameId> unmodifiableGameIdsByTeamName = Collections.unmodifiableMap(gameIdsByTeamName);
        return new Snapshot(unmodifiableStoredGames, unmodifiableGameIdsByTeamName);
    }

    private static Game createGame(StoredGame storedGame) {
        GameId id = storedGame.id();
        long sequenceNumber = id.value();
        TeamPair teams = storedGame.teams();
        Score score = storedGame.score();
        int scoreRevision = storedGame.scoreRevision();
        return new Game(id, sequenceNumber, teams, score, scoreRevision);
    }
}
