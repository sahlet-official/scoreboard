package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.usecases.stubs.SummaryQueryRepositoryStub;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import org.junit.jupiter.api.Test;

class SummaryQueryUseCaseTest {
    private static final GameId GAME_ID = new GameId(1);
    private static final long SEQUENCE_NUMBER = 1;
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");
    private static final Score SCORE = new Score(0, 5);
    private static final int SCORE_REVISION = 3;

    private static final Game GAME = new Game(
        GAME_ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION
    );

    private static final GameSummary GAME_SUMMARY = new GameSummary(TEAMS, SCORE);

    private final SummaryQueryRepositoryStub summaryQueryRepositoryStub = new SummaryQueryRepositoryStub();
    private final SummaryQueryUseCase summaryQueryUseCase = new SummaryQueryUseCase(summaryQueryRepositoryStub);

    @Test
    void shouldReturnEmptySummaryWhenNoGamesAreInProgress() {
        Summary summary = summaryQueryUseCase.execute();
        List<GameSummary> games = summary.games();

        assertThat(games).isEmpty();
    }

    @Test
    void shouldReturnTeamsAndScoreOfGameInProgress() {
        List<Game> gamesInProgress = List.of(GAME);
        summaryQueryRepositoryStub.setGames(gamesInProgress);

        Summary summary = summaryQueryUseCase.execute();
        List<GameSummary> games = summary.games();

        assertThat(games).containsExactly(GAME_SUMMARY);
    }
}
