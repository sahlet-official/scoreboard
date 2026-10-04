package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;

class TeamPairTest {
    @Test
    void shouldRejectEmptyHomeTeamName() {
        String emptyName = "";

        Throwable failure = catchThrowable(() -> new TeamPair(emptyName, "Canada"));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectEmptyAwayTeamName() {
        String emptyName = "";

        Throwable failure = catchThrowable(() -> new TeamPair("Mexico", emptyName));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }
}
