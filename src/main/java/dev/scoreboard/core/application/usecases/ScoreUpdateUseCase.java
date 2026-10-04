package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.ScoreUpdatePort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.exceptions.ScoreRevisionGapException;
import dev.scoreboard.core.application.ports.inbound.exceptions.StaleScoreRevisionException;
import dev.scoreboard.core.application.ports.inbound.models.UpdateScoreResult;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;

public class ScoreUpdateUseCase implements ScoreUpdatePort {
    private final GameRepository gameRepository;

    public ScoreUpdateUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public UpdateScoreResult execute(GameScore score) {
        ensureScoreIsNotNull(score);

        try {
            gameRepository.updateScoreIfNextRevision(score);
            return UpdateScoreResult.UPDATED;

        } catch (GameMissingException exception) {
            GameId gameId = exception.getGameId();
            throw new GameNotFoundException(gameId);

        } catch (ScoreRevisionConflictException exception) {
            Game currentGame = exception.getCurrentGame();
            GameScore currentScore = createGameScore(currentGame);
            return resolveRevisionConflict(score, currentScore);
        }
    }

    private static UpdateScoreResult resolveRevisionConflict(GameScore score, GameScore currentScore) {
        boolean currentScoreSentAgain = score.equals(currentScore);
        if (currentScoreSentAgain) {
            return UpdateScoreResult.UNCHANGED;
        }

        int receivedRevision = score.scoreRevision();
        int currentRevision = currentScore.scoreRevision();
        boolean revisionIsStale = receivedRevision <= currentRevision;

        if (revisionIsStale) {
            throw new StaleScoreRevisionException(receivedRevision, currentScore);
        }

        throw new ScoreRevisionGapException(receivedRevision, currentScore);
    }

    private static GameScore createGameScore(Game game) {
        GameId gameId = game.getId();
        Score score = game.getScore();
        int scoreRevision = game.getScoreRevision();
        return new GameScore(gameId, score, scoreRevision);
    }

    private static void ensureScoreIsNotNull(GameScore score) {
        if (score == null) {
            throw new NullPointerException("Score must not be null");
        }
    }
}
