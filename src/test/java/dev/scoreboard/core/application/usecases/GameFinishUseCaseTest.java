package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.usecases.stubs.GameFinishRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.GameId;
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
}
