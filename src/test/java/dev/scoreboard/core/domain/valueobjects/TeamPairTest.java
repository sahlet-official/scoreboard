package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;

class TeamPairTest {
    @Test
    void shouldRejectMissingHomeTeam() {
        TeamName missingTeam = null;
        TeamName canada = new TeamName("Canada");

        Throwable failure = catchThrowable(() -> new TeamPair(missingTeam, canada));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectMissingAwayTeam() {
        TeamName mexico = new TeamName("Mexico");
        TeamName missingTeam = null;

        Throwable failure = catchThrowable(() -> new TeamPair(mexico, missingTeam));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldKeepFirstTeamAsHomeTeam() {
        TeamName mexico = new TeamName("Mexico");
        TeamName canada = new TeamName("Canada");

        TeamPair teams = new TeamPair(mexico, canada);

        assertThat(teams.homeTeam()).isEqualTo(mexico);
    }

    @Test
    void shouldKeepSecondTeamAsAwayTeam() {
        TeamName mexico = new TeamName("Mexico");
        TeamName canada = new TeamName("Canada");

        TeamPair teams = new TeamPair(mexico, canada);

        assertThat(teams.awayTeam()).isEqualTo(canada);
    }

    @Test
    void shouldRejectSameTeamOnBothSides() {
        String name = "Mexico";
        TeamName mexico = new TeamName(name);
        TeamName sameTeam = new TeamName(name);

        Throwable failure = catchThrowable(() -> new TeamPair(mexico, sameTeam));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldTreatPairsWithSameTeamsAsSamePair() {
        TeamName mexico = new TeamName("Mexico");
        TeamName canada = new TeamName("Canada");
        TeamPair first = new TeamPair(mexico, canada);
        TeamPair second = new TeamPair(mexico, canada);

        boolean samePair = first.equals(second);

        assertThat(samePair).isTrue();
    }

    @Test
    void shouldTreatPairsWithDifferentTeamsAsDifferentPairs() {
        TeamPair first = new TeamPair(new TeamName("Mexico"), new TeamName("Canada"));
        TeamPair second = new TeamPair(new TeamName("Spain"), new TeamName("Brazil"));

        boolean samePair = first.equals(second);

        assertThat(samePair).isFalse();
    }

    @Test
    void shouldTreatPairsWithSameTeamsInReverseOrderAsDifferentPairs() {
        TeamName mexico = new TeamName("Mexico");
        TeamName canada = new TeamName("Canada");
        TeamPair mexicoAtHome = new TeamPair(mexico, canada);
        TeamPair canadaAtHome = new TeamPair(canada, mexico);

        boolean samePair = mexicoAtHome.equals(canadaAtHome);

        assertThat(samePair).isFalse();
    }

    @Test
    void shouldBeCreatedFromTeamNamesGivenAsText() {
        String mexico = "Mexico";
        String canada = "Canada";
        TeamPair expected = new TeamPair(new TeamName(mexico), new TeamName(canada));

        TeamPair teams = TeamPair.of(mexico, canada);

        assertThat(teams).isEqualTo(expected);
    }
}
