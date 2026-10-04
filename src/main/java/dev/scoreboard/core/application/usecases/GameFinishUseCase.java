package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.GameFinishPort;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.domain.valueobjects.GameId;

public class GameFinishUseCase implements GameFinishPort {
    private final GameRepository gameRepository;

    public GameFinishUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void execute(GameId gameId) {
        ensureGameIdIsNotNull(gameId);

        try {
            gameRepository.removeGame(gameId);

        } catch (GameMissingException exception) {
            throw new UnsupportedOperationException("Not implemented yet", exception);
        }
    }

    private static void ensureGameIdIsNotNull(GameId gameId) {
        if (gameId == null) {
            throw new NullPointerException("Game ID must not be null");
        }
    }
}
