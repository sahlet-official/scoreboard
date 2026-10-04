package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
    @ValueSource(ints = {0, 1, 3, Integer.MAX_VALUE})
    void shouldKeepGivenHomeScore(int homeScore) {
        Score score = new Score(homeScore, 0);

        assertThat(score.home()).isEqualTo(homeScore);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, Integer.MAX_VALUE})
    void shouldKeepGivenAwayScore(int awayScore) {
        Score score = new Score(0, awayScore);

        assertThat(score.away()).isEqualTo(awayScore);
    }

    @Test
    void shouldTreatSameScoresAsSameScore() {
        int home = 2;
        int away = 1;
        Score first = new Score(home, away);
        Score second = new Score(home, away);

        boolean sameScore = first.equals(second);

        assertThat(sameScore).isTrue();
    }
}
