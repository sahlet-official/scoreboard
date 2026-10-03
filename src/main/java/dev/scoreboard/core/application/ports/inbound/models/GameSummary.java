package dev.scoreboard.core.application.ports.inbound.models;

import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public record GameSummary(TeamPair teams, Score score) {
}
