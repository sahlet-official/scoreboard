package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.usecases.stubs.SummaryQueryRepositoryStub;
import java.util.List;
import org.junit.jupiter.api.Test;

class SummaryQueryUseCaseTest {
    private final SummaryQueryRepositoryStub summaryQueryRepositoryStub = new SummaryQueryRepositoryStub();
    private final SummaryQueryUseCase summaryQueryUseCase = new SummaryQueryUseCase(summaryQueryRepositoryStub);

    @Test
    void shouldReturnEmptySummaryWhenNoGamesAreInProgress() {
        Summary summary = summaryQueryUseCase.execute();
        List<GameSummary> games = summary.games();

        assertThat(games).isEmpty();
    }
}
