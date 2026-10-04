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

    @ParameterizedTest
    @ValueSource(strings = {
            "Costa Rica", "Bosnia and Herzegovina", "Korea  Republic", "Costa\tRica", "Costa\nRica"})
    void shouldAcceptNameWithWhitespaceInside(String nameWithInnerWhitespace) {
        TeamName teamName = new TeamName(nameWithInnerWhitespace);

        assertThat(teamName.value()).isEqualTo(nameWithInnerWhitespace);
    }

    @Test
    void shouldTreatSameNamesAsSameTeam() {
        TeamName first = new TeamName("Mexico");
        TeamName second = new TeamName("Mexico");

        boolean sameTeam = first.equals(second);

        assertThat(sameTeam).isTrue();
    }

    @Test
    void shouldTreatNamesInDifferentCaseAsDifferentTeams() {
        TeamName capitalized = new TeamName("Mexico");
        TeamName lowercase = new TeamName("mexico");

        boolean sameTeam = capitalized.equals(lowercase);

        assertThat(sameTeam).isFalse();
    }
}
