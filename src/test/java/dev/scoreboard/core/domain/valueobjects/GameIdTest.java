package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameIdTest {
    @ParameterizedTest
    @ValueSource(longs = {-1, -2, Long.MIN_VALUE})
    void shouldRejectNegativeId(long negativeId) {
        Throwable failure = catchThrowable(() -> new GameId(negativeId));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }
}
