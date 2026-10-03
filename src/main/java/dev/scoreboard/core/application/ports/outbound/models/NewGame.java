package dev.scoreboard.core.application.ports.outbound.models;

import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public record NewGame(TeamPair teams, Score score, int scoreRevision) {
}
