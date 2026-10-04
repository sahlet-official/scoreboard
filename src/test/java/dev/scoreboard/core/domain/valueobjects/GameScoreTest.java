package dev.scoreboard.core.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;

class GameScoreTest {
    @Test
    void shouldRejectMissingGameId() {
        GameId missingGameId = null;
        Score score = new Score(1, 0);
        int scoreRevision = 1;

        Throwable failure = catchThrowable(() -> new GameScore(missingGameId, score, scoreRevision));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectMissingScore() {
        GameId gameId = new GameId(7);
        Score missingScore = null;
        int scoreRevision = 1;

        Throwable failure = catchThrowable(() -> new GameScore(gameId, missingScore, scoreRevision));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }
}
