package dev.scoreboard.core.domain.valueobjects;

public record Score(int home, int away) {
    public Score {
        ensureIsNotNegative(home);
    }

    private static void ensureIsNotNegative(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Score must not be negative");
        }
    }
}
