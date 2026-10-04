package dev.scoreboard.core.domain.valueobjects;

public record TeamPair(String homeTeam, String awayTeam) {
    public TeamPair {
        ensureNameIsNotEmpty(homeTeam);
        ensureNameIsNotEmpty(awayTeam);
    }

    private static void ensureNameIsNotEmpty(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Team name must not be empty");
        }
    }
}
