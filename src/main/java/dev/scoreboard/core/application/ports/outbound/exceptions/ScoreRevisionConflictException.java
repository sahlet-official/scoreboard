package dev.scoreboard.core.application.ports.outbound.exceptions;

import dev.scoreboard.core.domain.entities.Game;

public class ScoreRevisionConflictException extends Exception {
    private final Game currentGame;

    public ScoreRevisionConflictException(Game currentGame) {
        super(messageFor(currentGame));
        this.currentGame = currentGame;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    private static String messageFor(Game currentGame) {
        long id = currentGame.getId().value();
        int currentRevision = currentGame.getScoreRevision();
        return "Score revision conflict: game id " + id
                + ", current revision " + currentRevision;
    }
}
