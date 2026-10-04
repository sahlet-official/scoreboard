package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.usecases.stubs.GameByTeamsQueryRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;

class GameByTeamsQueryUseCaseTest {
    private final GameByTeamsQueryRepositoryStub gameByTeamsQueryRepositoryStub =
        new GameByTeamsQueryRepositoryStub();
    private final GameByTeamsQueryUseCase gameByTeamsQueryUseCase =
        new GameByTeamsQueryUseCase(gameByTeamsQueryRepositoryStub);

    @Test
    void shouldRejectMissingTeams() {
        TeamPair missingTeams = null;

        Throwable failure = catchThrowable(() -> gameByTeamsQueryUseCase.execute(missingTeams));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }
}
