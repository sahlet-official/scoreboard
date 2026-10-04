package dev.scoreboard.core.application.ports.inbound;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public interface GameByTeamsQueryPort {
    GameDetails execute(TeamPair teams);
}
