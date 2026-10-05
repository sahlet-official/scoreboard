package dev.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface ScoreboardTest {
    Scoreboard scoreboard();

    @Test
    default void shouldHaveEmptySummaryWhenNoGamesWereStarted() {
        Summary summary = scoreboard().getSummary();
        List<GameSummary> gamesInSummary = summary.games();

        assertThat(gamesInSummary).isEmpty();
    }
}
