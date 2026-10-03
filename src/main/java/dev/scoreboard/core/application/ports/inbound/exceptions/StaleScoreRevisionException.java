package dev.scoreboard.core.application.ports.inbound.exceptions;

import dev.scoreboard.core.domain.valueobjects.GameScore;

public class StaleScoreRevisionException extends ScoreboardException {
    private final int receivedRevision;
    private final GameScore currentScore;

    public StaleScoreRevisionException(int receivedRevision, GameScore currentScore) {
        super(messageFor(receivedRevision, currentScore));
        this.receivedRevision = receivedRevision;
        this.currentScore = currentScore;
    }

    public int getReceivedRevision() {
        return receivedRevision;
    }

    public GameScore getCurrentScore() {
        return currentScore;
    }

    private static String messageFor(int receivedRevision, GameScore currentScore) {
        long id = currentScore.gameId().value();
        int currentRevision = currentScore.scoreRevision();
        return "Stale score revision: game id " + id
                + ", received revision " + receivedRevision
                + " is less than or equal to current revision " + currentRevision;
    }
}
