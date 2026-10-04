package dev.scoreboard.core.domain.valueobjects;

public record TeamName(String value) {
    public TeamName {
        ensureIsNotNull(value);
        ensureIsNotEmpty(value);
        ensureHasNoWhitespaceAtTheEdges(value);
    }

    private static void ensureIsNotNull(String value) {
        if (value == null) {
            throw new NullPointerException("Team name must not be null");
        }
    }

    private static void ensureIsNotEmpty(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Team name must not be empty");
        }
    }

    private static void ensureHasNoWhitespaceAtTheEdges(String value) {
        String stripped = value.strip();
        if (!value.equals(stripped)) {
            throw new IllegalArgumentException("Team name must not start or end with whitespace");
        }
    }
}
