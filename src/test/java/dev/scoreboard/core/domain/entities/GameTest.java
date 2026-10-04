package dev.scoreboard.core.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameTest {
    private static final GameId ID = new GameId(7);
    private static final long SEQUENCE_NUMBER = 7;
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");
    private static final Score SCORE = new Score(0, 0);
    private static final int SCORE_REVISION = 0;

    @Test
    void shouldRejectMissingId() {
        GameId missingId = null;

        Throwable failure = catchThrowable(
            () -> new Game(missingId, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectMissingTeams() {
        TeamPair missingTeams = null;

        Throwable failure = catchThrowable(
            () -> new Game(ID, SEQUENCE_NUMBER, missingTeams, SCORE, SCORE_REVISION)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectMissingScore() {
        Score missingScore = null;

        Throwable failure = catchThrowable(
            () -> new Game(ID, SEQUENCE_NUMBER, TEAMS, missingScore, SCORE_REVISION)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, Integer.MIN_VALUE})
    void shouldRejectNegativeScoreRevision(int negativeRevision) {
        Throwable failure = catchThrowable(
            () -> new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, negativeRevision)
        );

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }
}
