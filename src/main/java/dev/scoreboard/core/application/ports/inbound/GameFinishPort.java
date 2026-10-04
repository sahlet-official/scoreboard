package dev.scoreboard.core.application.ports.inbound;

import dev.scoreboard.core.domain.valueobjects.GameId;

public interface GameFinishPort {
    void execute(GameId gameId);
}
