package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.ScoreUpdatePort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.exceptions.ScoreRevisionGapException;
import dev.scoreboard.core.application.ports.inbound.exceptions.ScoreboardException;
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
    public UpdateScoreResult execute(GameScore gameScore) {
        ensureGameScoreIsNotNull(gameScore);

        try {
            gameRepository.updateScoreIfNextRevision(gameScore);
            return UpdateScoreResult.UPDATED;

        } catch (GameMissingException exception) {
            GameId gameId = exception.getGameId();
            throw new GameNotFoundException(gameId);

        } catch (ScoreRevisionConflictException exception) {
            Game currentGame = exception.getCurrentGame();
            GameScore currentGameScore = createGameScore(currentGame);
            return resolveRevisionConflict(gameScore, currentGameScore);
        }
    }

    private static UpdateScoreResult resolveRevisionConflict(GameScore gameScore, GameScore currentGameScore) {
        boolean currentScoreSentAgain = gameScore.equals(currentGameScore);
        if (currentScoreSentAgain) {
            return UpdateScoreResult.UNCHANGED;
        }

        throw createRevisionConflictException(gameScore, currentGameScore);
    }

    private static ScoreboardException createRevisionConflictException(
            GameScore gameScore, GameScore currentGameScore) {
        int receivedRevision = gameScore.scoreRevision();
        int currentRevision = currentGameScore.scoreRevision();

        boolean revisionIsStale = receivedRevision <= currentRevision;
        if (revisionIsStale) {
            return new StaleScoreRevisionException(receivedRevision, currentGameScore);
        }

        return new ScoreRevisionGapException(receivedRevision, currentGameScore);
    }

    private static GameScore createGameScore(Game game) {
        GameId gameId = game.getId();
        Score score = game.getScore();
        int scoreRevision = game.getScoreRevision();
        return new GameScore(gameId, score, scoreRevision);
    }

    private static void ensureGameScoreIsNotNull(GameScore gameScore) {
        if (gameScore == null) {
            throw new NullPointerException("Game score must not be null");
        }
    }
}
