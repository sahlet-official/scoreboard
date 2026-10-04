package dev.scoreboard.core.domain.valueobjects;

public record TeamPair(TeamName homeTeam, TeamName awayTeam) {
    public TeamPair {
        ensureTeamIsNotNull(homeTeam);
        ensureTeamIsNotNull(awayTeam);
        ensureTeamsAreDifferent(homeTeam, awayTeam);
    }

    public static TeamPair of(String homeTeamName, String awayTeamName) {
        TeamName homeTeam = new TeamName(homeTeamName);
        TeamName awayTeam = new TeamName(awayTeamName);
        return new TeamPair(homeTeam, awayTeam);
    }

    private static void ensureTeamIsNotNull(TeamName team) {
        if (team == null) {
            throw new NullPointerException("Team must not be null");
        }
    }

    private static void ensureTeamsAreDifferent(TeamName homeTeam, TeamName awayTeam) {
        boolean sameTeam = homeTeam.equals(awayTeam);
        if (sameTeam) {
            throw new IllegalArgumentException("Home team and away team must be different");
        }
    }
}
