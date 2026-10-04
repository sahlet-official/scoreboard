package dev.scoreboard.core.domain.valueobjects;

public record TeamPair(String homeTeam, String awayTeam) {
    public TeamPair {
        ensureNameIsValid(homeTeam);
        ensureNameIsValid(awayTeam);
    }

    private static void ensureNameIsValid(String name) {
        ensureNameIsNotNull(name);
        ensureNameIsNotEmpty(name);
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
}
