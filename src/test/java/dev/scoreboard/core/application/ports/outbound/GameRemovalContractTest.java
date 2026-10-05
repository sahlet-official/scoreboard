package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.domain.valueobjects.GameId;
import org.junit.jupiter.api.Test;

public interface GameRemovalContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldRejectRemovalOfGameThatWasNotAdded() {
        Throwable failure = tryToRemoveGame(UNKNOWN_GAME_ID);

        assertThat(failure).isInstanceOf(GameMissingException.class);
    }

    private Throwable tryToRemoveGame(GameId id) {
        return catchThrowable(() -> gameRepository().removeGame(id));
    }
}
