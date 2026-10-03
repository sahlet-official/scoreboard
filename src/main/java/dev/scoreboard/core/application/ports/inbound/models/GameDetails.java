package dev.scoreboard.core.application.ports.inbound.models;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public record GameDetails(GameId id, TeamPair teams, Score score, int scoreRevision) {
}
