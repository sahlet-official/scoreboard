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
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryGameRepository implements GameRepository {
    private final Map<GameId, Game> games = new HashMap<>();
    private long nextId = 1;

    @Override
    public Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException {
        GameId id = new GameId(nextId);
        long sequenceNumber = nextId;
        nextId++;

        TeamPair teams = game.teams();
        Score score = game.score();
        int scoreRevision = game.scoreRevision();
        Game addedGame = new Game(id, sequenceNumber, teams, score, scoreRevision);

        games.put(id, addedGame);
        return addedGame;
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Game> findAllGames() {
        Collection<Game> allGames = games.values();
        return List.copyOf(allGames);
    }
}
