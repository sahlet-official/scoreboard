package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.usecases.stubs.ScoreUpdateRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScoreUpdateUseCaseTest {
    private static final GameId GAME_ID = new GameId(7);
    private static final GameScore NEW_SCORE = new GameScore(GAME_ID, new Score(1, 0), 1);

    private final ScoreUpdateRepositoryStub scoreUpdateRepositoryStub = new ScoreUpdateRepositoryStub();
    private final ScoreUpdateUseCase scoreUpdateUseCase = new ScoreUpdateUseCase(scoreUpdateRepositoryStub);

    @Test
    void shouldRejectMissingScore() {
        GameScore missingScore = null;

        Throwable failure = catchThrowable(() -> scoreUpdateUseCase.execute(missingScore));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectUpdateWhenGameIsNotFound() {
        Throwable failure = catchThrowable(() -> scoreUpdateUseCase.execute(NEW_SCORE));

        assertThat(failure).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldReportIdOfGameThatIsNotFound() {
        GameNotFoundException exception = catchThrowableOfType(
            GameNotFoundException.class,
            () -> scoreUpdateUseCase.execute(NEW_SCORE)
        );
        Optional<GameId> reportedId = exception.getGameId();

        assertThat(reportedId).contains(GAME_ID);
    }
}
