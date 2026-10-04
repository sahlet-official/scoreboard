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

public class GameFinishRepositoryStub implements GameRepository {
    private boolean gameMissing;
    private GameId removedGameId;

    public void setGameMissing(boolean gameMissing) {
        this.gameMissing = gameMissing;
    }

    public GameId getRemovedGameId() {
        return removedGameId;
    }

    @Override
    public void removeGame(GameId id) throws GameMissingException {
        if (gameMissing) {
            throw new GameMissingException(id);
        }

        removedGameId = id;
    }

    @Override
    public Game addGameWithUniqueTeams(NewGame game) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateScoreIfNextRevision(GameScore score) {
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
