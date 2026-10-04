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
}
