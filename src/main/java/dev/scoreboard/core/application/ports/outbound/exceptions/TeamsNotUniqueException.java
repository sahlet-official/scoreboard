package dev.scoreboard.core.application.ports.outbound.exceptions;

import dev.scoreboard.core.domain.entities.Game;

public class TeamsNotUniqueException extends Exception {
    private final Game conflictingGame;

    public TeamsNotUniqueException(Game conflictingGame) {
        super(messageFor(conflictingGame));
        this.conflictingGame = conflictingGame;
    }

    public Game getConflictingGame() {
        return conflictingGame;
    }

    private static String messageFor(Game conflictingGame) {
        long id = conflictingGame.getId().value();
        return "Teams not unique: one of the teams is playing in game id " + id;
    }
}
