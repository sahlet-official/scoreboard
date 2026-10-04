package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.SCORE;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.SCORE_REVISION;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.UNKNOWN_GAME_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import org.junit.jupiter.api.Test;

public interface ScoreUpdateContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldRejectScoreOfGameThatWasNotAdded() {
        GameScore scoreOfUnknownGame = new GameScore(UNKNOWN_GAME_ID, SCORE, SCORE_REVISION);

        Throwable failure = catchThrowable(
            () -> gameRepository().updateScoreIfNextRevision(scoreOfUnknownGame)
        );

        assertThat(failure).isInstanceOf(GameMissingException.class);
    }
}
