package dev.scoreboard.core.application.ports.outbound.exceptions;

import dev.scoreboard.core.domain.valueobjects.GameId;

public class GameMissingException extends Exception {
    private final GameId gameId;

    public GameMissingException(GameId gameId) {
        super(messageFor(gameId));
        this.gameId = gameId;
    }

    public GameId getGameId() {
        return gameId;
    }

    private static String messageFor(GameId gameId) {
        long id = gameId.value();
        return "There is no active game with id " + id;
    }
}
