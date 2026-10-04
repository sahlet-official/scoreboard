package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameIdTest {
    @ParameterizedTest
    @ValueSource(longs = {-1, -2, Long.MIN_VALUE})
    void shouldRejectNegativeId(long negativeId) {
        Throwable failure = catchThrowable(() -> new GameId(negativeId));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 42, Long.MAX_VALUE})
    void shouldKeepGivenValue(long value) {
        GameId gameId = new GameId(value);

        assertThat(gameId.value()).isEqualTo(value);
    }

    @Test
    void shouldTreatSameValuesAsSameId() {
        long value = 7;
        GameId first = new GameId(value);
        GameId second = new GameId(value);

        boolean sameId = first.equals(second);

        assertThat(sameId).isTrue();
    }

    @Test
    void shouldTreatDifferentValuesAsDifferentIds() {
        GameId first = new GameId(7);
        GameId second = new GameId(8);

        boolean sameId = first.equals(second);

        assertThat(sameId).isFalse();
    }
}
