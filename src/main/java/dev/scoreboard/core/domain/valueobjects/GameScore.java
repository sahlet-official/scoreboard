package dev.scoreboard.core.domain.valueobjects;

public record GameScore(GameId gameId, Score score, int scoreRevision) {
    public GameScore {
        ensureGameIdIsNotNull(gameId);
        ensureScoreIsNotNull(score);
        ensureScoreRevisionIsNotNegative(scoreRevision);
    }

    private static void ensureGameIdIsNotNull(GameId gameId) {
        if (gameId == null) {
            throw new NullPointerException("Game ID must not be null");
        }
    }

    private static void ensureScoreIsNotNull(Score score) {
        if (score == null) {
            throw new NullPointerException("Score must not be null");
        }
    }

    private static void ensureScoreRevisionIsNotNegative(int scoreRevision) {
        if (scoreRevision < 0) {
            throw new IllegalArgumentException("Score revision must not be negative");
        }
    }
}
