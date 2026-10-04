package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.ScoreUpdatePort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.models.UpdateScoreResult;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;

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
            return UpdateScoreResult.UNCHANGED;

        } catch (GameMissingException exception) {
            GameId gameId = exception.getGameId();
            throw new GameNotFoundException(gameId);

        } catch (ScoreRevisionConflictException exception) {
            throw new UnsupportedOperationException("Not implemented yet", exception);
        }
    }

    private static void ensureScoreIsNotNull(GameScore score) {
        if (score == null) {
            throw new NullPointerException("Score must not be null");
        }
    }
}
