package dev.scoreboard.core.application.usecases.mappers;

import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public final class GameSummaryMapper {
    private GameSummaryMapper() {
    }

    public static GameSummary createGameSummary(Game game) {
        TeamPair teams = game.getTeams();
        Score score = game.getScore();
        return new GameSummary(teams, score);
    }
}
