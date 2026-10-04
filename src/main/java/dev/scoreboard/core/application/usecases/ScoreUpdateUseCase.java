package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.ScoreUpdatePort;
import dev.scoreboard.core.application.ports.inbound.models.UpdateScoreResult;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.domain.valueobjects.GameScore;

public class ScoreUpdateUseCase implements ScoreUpdatePort {
    private final GameRepository gameRepository;

    public ScoreUpdateUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public UpdateScoreResult execute(GameScore score) {
        ensureScoreIsNotNull(score);
        return UpdateScoreResult.UNCHANGED;
    }

    private static void ensureScoreIsNotNull(GameScore score) {
        if (score == null) {
            throw new NullPointerException("Score must not be null");
        }
    }
}
