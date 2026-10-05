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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryGameRepository implements GameRepository {
    private record StoredGame(GameId id, TeamPair teams, Score score, int scoreRevision) {
    }

    private final Map<GameId, StoredGame> storedGames = new HashMap<>();
    private final Map<TeamName, GameId> gameIdsByTeamName = new HashMap<>();
    private long nextId = 1;

    @Override
    public synchronized Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException {
        TeamPair teams = game.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();
        ensureTeamIsNotPlaying(homeTeam);
        ensureTeamIsNotPlaying(awayTeam);

        GameId id = new GameId(nextId);
        nextId++;

        Score score = game.score();
        int scoreRevision = game.scoreRevision();
        StoredGame storedGame = new StoredGame(id, teams, score, scoreRevision);

        storedGames.put(id, storedGame);
        gameIdsByTeamName.put(homeTeam, id);
        gameIdsByTeamName.put(awayTeam, id);

        return createGame(storedGame);
    }

    private void ensureTeamIsNotPlaying(TeamName teamName) throws TeamsNotUniqueException {
        GameId idOfGameWithTeam = gameIdsByTeamName.get(teamName);
        if (idOfGameWithTeam == null) {
            return;
        }

        StoredGame storedGameWithTeam = storedGames.get(idOfGameWithTeam);
        Game gameWithTeam = createGame(storedGameWithTeam);
        throw new TeamsNotUniqueException(gameWithTeam);
    }

    @Override
    public synchronized void updateScoreIfNextRevision(GameScore gameScore)
            throws GameMissingException, ScoreRevisionConflictException {
        GameId gameId = gameScore.gameId();
        StoredGame storedGame = storedGames.get(gameId);
        if (storedGame == null) {
            throw new GameMissingException(gameId);
        }

        int storedScoreRevision = storedGame.scoreRevision();
        int newScoreRevision = gameScore.scoreRevision();
        boolean revisionIsNext = newScoreRevision - 1 == storedScoreRevision;
        if (!revisionIsNext) {
            Game currentGame = createGame(storedGame);
            throw new ScoreRevisionConflictException(currentGame);
        }

        TeamPair teams = storedGame.teams();
        Score newScore = gameScore.score();
        StoredGame updatedGame = new StoredGame(gameId, teams, newScore, newScoreRevision);
        storedGames.put(gameId, updatedGame);
    }

    @Override
    public void removeGame(GameId id) throws GameMissingException {
        StoredGame storedGame = storedGames.get(id);
        if (storedGame == null) {
            throw new GameMissingException(id);
        }

        TeamPair teams = storedGame.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();

        storedGames.remove(id);
        gameIdsByTeamName.remove(homeTeam);
        gameIdsByTeamName.remove(awayTeam);
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        StoredGame storedGame = storedGames.get(id);
        if (storedGame == null) {
            return Optional.empty();
        }

        Game game = createGame(storedGame);
        return Optional.of(game);
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        TeamName homeTeam = teams.homeTeam();
        GameId idOfGameWithHomeTeam = gameIdsByTeamName.get(homeTeam);
        if (idOfGameWithHomeTeam == null) {
            return Optional.empty();
        }

        StoredGame storedGameWithHomeTeam = storedGames.get(idOfGameWithHomeTeam);
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
        Collection<StoredGame> allStoredGames = storedGames.values();

        List<Game> games = new ArrayList<>();
        for (StoredGame storedGame : allStoredGames) {
            Game game = createGame(storedGame);
            games.add(game);
        }

        return games;
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
