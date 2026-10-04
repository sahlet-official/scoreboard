package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ScoreTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, Integer.MIN_VALUE})
    void shouldRejectNegativeHomeScore(int negativeScore) {
        Throwable failure = catchThrowable(() -> new Score(negativeScore, 0));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, Integer.MIN_VALUE})
    void shouldRejectNegativeAwayScore(int negativeScore) {
        Throwable failure = catchThrowable(() -> new Score(0, negativeScore));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "1, 0", "0, 1", "3, 2", Integer.MAX_VALUE + ", " + Integer.MAX_VALUE})
    void shouldAcceptNonNegativeScore(int home, int away) {
        Score score = new Score(home, away);

        assertThat(score.home()).isEqualTo(home);
        assertThat(score.away()).isEqualTo(away);
    }
}
