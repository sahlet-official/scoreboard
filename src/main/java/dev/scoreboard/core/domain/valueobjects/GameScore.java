package dev.scoreboard.core.domain.valueobjects;

public record GameScore(GameId gameId, Score score, int scoreRevision) {
    public GameScore {
        ensureGameIdIsNotNull(gameId);
    }

    private static void ensureGameIdIsNotNull(GameId gameId) {
        if (gameId == null) {
            throw new NullPointerException("Game ID must not be null");
        }
    }
}
