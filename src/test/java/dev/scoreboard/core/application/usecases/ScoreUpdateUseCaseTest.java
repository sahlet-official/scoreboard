package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.usecases.stubs.ScoreUpdateRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import org.junit.jupiter.api.Test;

class ScoreUpdateUseCaseTest {
    private final ScoreUpdateRepositoryStub scoreUpdateRepositoryStub = new ScoreUpdateRepositoryStub();
    private final ScoreUpdateUseCase scoreUpdateUseCase = new ScoreUpdateUseCase(scoreUpdateRepositoryStub);

    @Test
    void shouldRejectMissingScore() {
        GameScore missingScore = null;

        Throwable failure = catchThrowable(() -> scoreUpdateUseCase.execute(missingScore));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }
}
