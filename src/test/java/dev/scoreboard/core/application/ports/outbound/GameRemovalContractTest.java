package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public interface GameRemovalContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldRejectRemovalOfGameThatWasNotAdded() {
        Throwable failure = tryToRemoveGame(UNKNOWN_GAME_ID);

        assertThat(failure).isInstanceOf(GameMissingException.class);
    }

    @Test
    default void shouldReportIdOfGameThatWasNotAddedWhenRemovalIsRejected() {
        GameMissingException exception = (GameMissingException) tryToRemoveGame(UNKNOWN_GAME_ID);
        GameId reportedId = exception.getGameId();

        assertThat(reportedId).isEqualTo(UNKNOWN_GAME_ID);
    }

    @Test
    default void shouldNotFindRemovedGameById() {
        GameId id = addNewGame();

        tryToRemoveGame(id);
        Optional<Game> foundGame = gameRepository().findGame(id);

        assertThat(foundGame).isEmpty();
    }

    @Test
    default void shouldNotFindRemovedGameByTeams() {
        GameId id = addNewGame();

        tryToRemoveGame(id);
        Optional<Game> foundGame = gameRepository().findGame(NEW_GAME.teams());

        assertThat(foundGame).isEmpty();
    }

    private GameId addNewGame() {
        try {
            Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
            return addedGame.getId();
        } catch (TeamsNotUniqueException exception) {
            throw new AssertionError("New game was rejected", exception);
        }
    }

    private Throwable tryToRemoveGame(GameId id) {
        return catchThrowable(() -> gameRepository().removeGame(id));
    }
}
