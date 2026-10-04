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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryGameRepository implements GameRepository {
    private final Map<GameId, Game> games = new HashMap<>();
    private final Map<TeamName, GameId> gameIdsByTeamName = new HashMap<>();
    private long nextId = 1;

    @Override
    public Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException {
        TeamPair teams = game.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();
        ensureTeamIsNotPlaying(homeTeam);

        GameId id = new GameId(nextId);
        long sequenceNumber = nextId;
        nextId++;

        Score score = game.score();
        int scoreRevision = game.scoreRevision();
        Game addedGame = new Game(id, sequenceNumber, teams, score, scoreRevision);

        games.put(id, addedGame);
        gameIdsByTeamName.put(homeTeam, id);
        gameIdsByTeamName.put(awayTeam, id);

        return addedGame;
    }

    private void ensureTeamIsNotPlaying(TeamName teamName) throws TeamsNotUniqueException {
        GameId idOfGameWithTeam = gameIdsByTeamName.get(teamName);
        if (idOfGameWithTeam == null) {
            return;
        }

        Game gameWithTeam = games.get(idOfGameWithTeam);
        throw new TeamsNotUniqueException(gameWithTeam);
    }

    @Override
    public void updateScoreIfNextRevision(GameScore score)
            throws GameMissingException, ScoreRevisionConflictException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeGame(GameId id) throws GameMissingException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        Game game = games.get(id);
        return Optional.ofNullable(game);
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        TeamName homeTeam = teams.homeTeam();
        GameId idOfGameWithHomeTeam = gameIdsByTeamName.get(homeTeam);
        if (idOfGameWithHomeTeam == null) {
            return Optional.empty();
        }

        Game gameWithHomeTeam = games.get(idOfGameWithHomeTeam);
        TeamPair teamsOfGame = gameWithHomeTeam.getTeams();
        boolean found = teams.equals(teamsOfGame);
        if (!found) {
            return Optional.empty();
        }

        return Optional.of(gameWithHomeTeam);
    }

    @Override
    public List<Game> findAllGames() {
        Collection<Game> allGames = games.values();
        return List.copyOf(allGames);
    }
}
