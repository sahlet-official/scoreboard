package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.ANOTHER_SCORE;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.NEW_GAME;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.NEXT_SCORE_REVISION;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.SCORE;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.SCORE_REVISION;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.UNKNOWN_GAME_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;
import java.util.Optional;
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

    @Test
    default void shouldReportIdOfGameThatWasNotAddedWhenScoreIsRejected() {
        GameScore scoreOfUnknownGame = new GameScore(UNKNOWN_GAME_ID, SCORE, SCORE_REVISION);

        GameMissingException exception = catchThrowableOfType(
            GameMissingException.class,
            () -> gameRepository().updateScoreIfNextRevision(scoreOfUnknownGame)
        );
        GameId reportedId = exception.getGameId();

        assertThat(reportedId).isEqualTo(UNKNOWN_GAME_ID);
    }

    @Test
    default void shouldStoreScoreSentWithNextRevision()
            throws TeamsNotUniqueException, GameMissingException, ScoreRevisionConflictException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        GameId id = addedGame.getId();
        GameScore scoreWithNextRevision = new GameScore(id, ANOTHER_SCORE, NEXT_SCORE_REVISION);

        gameRepository().updateScoreIfNextRevision(scoreWithNextRevision);
        Optional<Game> foundGame = gameRepository().findGame(id);
        Optional<Score> storedScore = foundGame.map(Game::getScore);

        assertThat(storedScore).contains(ANOTHER_SCORE);
    }
}
