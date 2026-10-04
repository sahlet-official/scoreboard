package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.usecases.stubs.GameFinishRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.GameId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameFinishUseCaseTest {
    private static final GameId GAME_ID = new GameId(7);

    private final GameFinishRepositoryStub gameFinishRepositoryStub = new GameFinishRepositoryStub();
    private final GameFinishUseCase gameFinishUseCase = new GameFinishUseCase(gameFinishRepositoryStub);

    @Test
    void shouldRejectMissingGameId() {
        GameId missingGameId = null;

        Throwable failure = catchThrowable(() -> gameFinishUseCase.execute(missingGameId));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRemoveFinishedGameFromRepository() {
        gameFinishUseCase.execute(GAME_ID);
        GameId removedGameId = gameFinishRepositoryStub.getRemovedGameId();

        assertThat(removedGameId).isEqualTo(GAME_ID);
    }

    @Test
    void shouldRejectFinishWhenGameIsNotFound() {
        gameFinishRepositoryStub.setGameMissing(true);

        Throwable failure = catchThrowable(() -> gameFinishUseCase.execute(GAME_ID));

        assertThat(failure).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldReportIdOfGameThatIsNotFound() {
        gameFinishRepositoryStub.setGameMissing(true);

        GameNotFoundException exception = catchThrowableOfType(
            GameNotFoundException.class,
            () -> gameFinishUseCase.execute(GAME_ID)
        );
        Optional<GameId> reportedId = exception.getGameId();

        assertThat(reportedId).contains(GAME_ID);
    }
}
