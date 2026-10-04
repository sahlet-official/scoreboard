package dev.scoreboard.core.application.usecases.stubs;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

public class ScoreUpdateRepositoryStub implements GameRepository {
    @Override
    public Game addGameWithUniqueTeams(NewGame game) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateScoreIfNextRevision(GameScore score) throws GameMissingException {
        GameId gameId = score.gameId();
        throw new GameMissingException(gameId);
    }

    @Override
    public void removeGame(GameId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Game> findAllGames() {
        throw new UnsupportedOperationException();
    }
}
