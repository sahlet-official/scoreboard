package dev.scoreboard.core.application.ports.inbound;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.domain.valueobjects.GameId;

public interface GameQueryPort {
    GameDetails execute(GameId gameId);
}
