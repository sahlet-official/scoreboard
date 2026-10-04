package dev.scoreboard.core.application.ports.inbound;

import dev.scoreboard.core.application.ports.inbound.models.UpdateScoreResult;
import dev.scoreboard.core.domain.valueobjects.GameScore;

public interface ScoreUpdatePort {
    UpdateScoreResult execute(GameScore score);
}
