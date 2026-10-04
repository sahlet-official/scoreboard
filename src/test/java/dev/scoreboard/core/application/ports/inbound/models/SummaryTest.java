package dev.scoreboard.core.application.ports.inbound.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SummaryTest {
    @Test
    void shouldNotAllowChangingGames() {
        List<GameSummary> gamesInSummary = gamesOfEmptySummary();
        GameSummary anotherGame = gameBetweenMexicoAndCanada();

        Throwable failure = catchThrowable(() -> gamesInSummary.add(anotherGame));

        assertThat(failure).isInstanceOf(UnsupportedOperationException.class);
    }

    private static List<GameSummary> gamesOfEmptySummary() {
        List<GameSummary> games = new ArrayList<>();
        Summary summary = new Summary(games);
        return summary.games();
    }

    private static GameSummary gameBetweenMexicoAndCanada() {
        TeamPair teams = TeamPair.of("Mexico", "Canada");
        Score score = new Score(0, 0);
        return new GameSummary(teams, score);
    }
}
