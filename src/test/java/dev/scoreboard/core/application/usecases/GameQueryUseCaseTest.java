package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.usecases.stubs.GameQueryRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.GameId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameQueryUseCaseTest {
    private static final GameId GAME_ID = new GameId(7);

    private final GameQueryRepositoryStub gameQueryRepositoryStub = new GameQueryRepositoryStub();
    private final GameQueryUseCase gameQueryUseCase = new GameQueryUseCase(gameQueryRepositoryStub);

    @Test
    void shouldRejectMissingGameId() {
        GameId missingGameId = null;

        Throwable failure = catchThrowable(() -> gameQueryUseCase.execute(missingGameId));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectQueryWhenGameIsNotFound() {
        Throwable failure = catchThrowable(() -> gameQueryUseCase.execute(GAME_ID));

        assertThat(failure).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldReportIdOfGameThatIsNotFound() {
        GameNotFoundException exception = catchThrowableOfType(
            GameNotFoundException.class,
            () -> gameQueryUseCase.execute(GAME_ID)
        );
        Optional<GameId> reportedId = exception.getGameId();

        assertThat(reportedId).contains(GAME_ID);
    }
}
