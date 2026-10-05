package dev.scoreboard.core.application.ports.outbound.gamerepository;

import static dev.scoreboard.core.application.ports.outbound.gamerepository.GameRepositoryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public interface ScoreUpdateContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldRejectScoreOfGameThatWasNotAdded() {
        GameScore scoreOfUnknownGame = new GameScore(UNKNOWN_GAME_ID, SCORE, SCORE_REVISION);

        Throwable failure = tryToUpdateScore(scoreOfUnknownGame);

        assertThat(failure).isInstanceOf(GameMissingException.class);
    }

    @Test
    default void shouldReportIdOfGameThatWasNotAddedWhenScoreIsRejected() {
        GameScore scoreOfUnknownGame = new GameScore(UNKNOWN_GAME_ID, SCORE, SCORE_REVISION);

        GameMissingException exception = (GameMissingException) tryToUpdateScore(scoreOfUnknownGame);
        GameId reportedId = exception.getGameId();

        assertThat(reportedId).isEqualTo(UNKNOWN_GAME_ID);
    }

    @Test
    default void shouldStoreScoreSentWithNextRevision() {
        GameId id = addNewGame();
        GameScore scoreWithNextRevision = new GameScore(id, ANOTHER_SCORE, NEXT_SCORE_REVISION);

        tryToUpdateScore(scoreWithNextRevision);
        Score storedScore = findStoredGame().getScore();

        assertThat(storedScore).isEqualTo(ANOTHER_SCORE);
    }

    @Test
    default void shouldStoreNextRevisionSentWithScore() {
        GameId id = addNewGame();
        GameScore scoreWithNextRevision = new GameScore(id, ANOTHER_SCORE, NEXT_SCORE_REVISION);

        tryToUpdateScore(scoreWithNextRevision);
        int storedScoreRevision = findStoredGame().getScoreRevision();

        assertThat(storedScoreRevision).isEqualTo(NEXT_SCORE_REVISION);
    }

    @ParameterizedTest
    @ValueSource(ints = {
        0, SCORE_REVISION - 1, SCORE_REVISION, NEXT_SCORE_REVISION + 1, Integer.MAX_VALUE
    })
    default void shouldRejectScoreSentWithRevisionThatIsNotNext(int revisionThatIsNotNext) {
        GameId id = addNewGame();
        GameScore gameScore = new GameScore(id, ANOTHER_SCORE, revisionThatIsNotNext);

        Throwable failure = tryToUpdateScore(gameScore);

        assertThat(failure).isInstanceOf(ScoreRevisionConflictException.class);
    }

    @Test
    default void shouldReportCurrentGameWhenScoreIsRejected() {
        GameId id = addNewGame();
        GameScore scoreWithCurrentRevision = new GameScore(id, ANOTHER_SCORE, NEW_GAME.scoreRevision());

        ScoreRevisionConflictException exception =
            (ScoreRevisionConflictException) tryToUpdateScore(scoreWithCurrentRevision);
        Game currentGame = exception.getCurrentGame();

        boolean reportsId = currentGame.getId().equals(id);
        boolean reportsScore = currentGame.getScore().equals(NEW_GAME.score());
        boolean reportsScoreRevision = currentGame.getScoreRevision() == NEW_GAME.scoreRevision();
        boolean reportsCurrentGame = reportsId && reportsScore && reportsScoreRevision;

        assertThat(reportsCurrentGame).isTrue();
    }

    @Test
    default void shouldKeepStoredScoreWhenScoreIsRejected() {
        GameId id = addNewGame();
        GameScore scoreWithCurrentRevision = new GameScore(id, ANOTHER_SCORE, NEW_GAME.scoreRevision());

        tryToUpdateScore(scoreWithCurrentRevision);
        Score storedScore = findStoredGame().getScore();

        assertThat(storedScore).isEqualTo(NEW_GAME.score());
    }

    @Test
    default void shouldKeepStoredScoreRevisionWhenScoreIsRejected() {
        GameId id = addNewGame();
        GameScore scoreWithRevisionAfterNext = new GameScore(id, ANOTHER_SCORE, NEXT_SCORE_REVISION + 1);

        tryToUpdateScore(scoreWithRevisionAfterNext);
        int storedScoreRevision = findStoredGame().getScoreRevision();

        assertThat(storedScoreRevision).isEqualTo(NEW_GAME.scoreRevision());
    }

    private GameId addNewGame() {
        try {
            Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
            return addedGame.getId();
        } catch (TeamsNotUniqueException exception) {
            throw new AssertionError("New game was rejected", exception);
        }
    }

    private Throwable tryToUpdateScore(GameScore gameScore) {
        return catchThrowable(() -> gameRepository().updateScoreIfNextRevision(gameScore));
    }

    private Game findStoredGame() {
        Optional<Game> foundGame = gameRepository().findGame(NEW_GAME.teams());
        return foundGame.orElseThrow();
    }
}
