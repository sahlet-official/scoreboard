package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.usecases.stubs.GameQueryRepositoryStub;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameQueryUseCaseTest {
    private static final GameId GAME_ID = new GameId(7);
    private static final long SEQUENCE_NUMBER = 7;
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");
    private static final Score SCORE = new Score(2, 1);
    private static final int SCORE_REVISION = 5;

    private static final Game GAME = new Game(
        GAME_ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION
    );

    private static final GameDetails GAME_DETAILS = new GameDetails(
        GAME_ID, TEAMS, SCORE, SCORE_REVISION
    );

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

    @Test
    void shouldReturnDetailsOfFoundGame() {
        gameQueryRepositoryStub.setGame(GAME);

        GameDetails gameDetails = gameQueryUseCase.execute(GAME_ID);

        assertThat(gameDetails).isEqualTo(GAME_DETAILS);
    }
}
