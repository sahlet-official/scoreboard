package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TeamNameTest {
    @Test
    void shouldRejectMissingName() {
        String missingName = null;

        Throwable failure = catchThrowable(() -> new TeamName(missingName));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectEmptyName() {
        String emptyName = "";

        Throwable failure = catchThrowable(() -> new TeamName(emptyName));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Mexico", "Mexico ", " Mexico ", " Mexi co ", "\tMexico", "Mexico\n", " Mexi\nco ", " "})
    void shouldRejectNameWithWhitespaceAtTheEdges(String nameWithEdgeWhitespace) {
        Throwable failure = catchThrowable(() -> new TeamName(nameWithEdgeWhitespace));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }
}
