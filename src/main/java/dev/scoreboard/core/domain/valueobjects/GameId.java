package dev.scoreboard.core.domain.valueobjects;

public record GameId(long value) {
    public GameId {
        ensureIsNotNegative(value);
    }

    private static void ensureIsNotNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Game ID must not be negative");
        }
    }
}
