package dev.scoreboard.core.domain.valueobjects;

public record TeamPair(String homeTeam, String awayTeam) {
    public TeamPair {
        ensureNameIsValid(homeTeam);
        ensureNameIsNotNull(awayTeam);
        ensureNameIsNotEmpty(awayTeam);
    }

    private static void ensureNameIsValid(String name) {
        ensureNameIsNotNull(name);
        ensureNameIsNotEmpty(name);
        ensureNameHasNoWhitespaceAtTheEdges(name);
    }

    private static void ensureNameIsNotNull(String name) {
        if (name == null) {
            throw new NullPointerException("Team name must not be null");
        }
    }

    private static void ensureNameIsNotEmpty(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Team name must not be empty");
        }
    }

    private static void ensureNameHasNoWhitespaceAtTheEdges(String name) {
        String stripped = name.strip();
        if (!name.equals(stripped)) {
            throw new IllegalArgumentException("Team name must not start or end with whitespace");
        }
    }
}
