package dev.scoreboard.core.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;

class GameTest {
    @Test
    void shouldRejectMissingId() {
        GameId missingId = null;
        long sequenceNumber = 7;
        TeamPair teams = TeamPair.of("Mexico", "Canada");
        Score score = new Score(0, 0);
        int scoreRevision = 0;

        Throwable failure = catchThrowable(
            () -> new Game(missingId, sequenceNumber, teams, score, scoreRevision)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }
}
