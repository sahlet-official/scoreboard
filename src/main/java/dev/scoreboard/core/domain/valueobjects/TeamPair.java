package dev.scoreboard.core.domain.valueobjects;

public record TeamPair(TeamName homeTeam, TeamName awayTeam) {
    public TeamPair {
        ensureTeamIsNotNull(homeTeam);
        ensureTeamIsNotNull(awayTeam);
    }

    private static void ensureTeamIsNotNull(TeamName team) {
        if (team == null) {
            throw new NullPointerException("Team must not be null");
        }
    }
}
