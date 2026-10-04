package dev.scoreboard.core.application.usecases.mappers;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public final class GameDetailsMapper {
    private GameDetailsMapper() {
    }

    public static GameDetails createGameDetails(Game game) {
        GameId id = game.getId();
        TeamPair teams = game.getTeams();
        Score score = game.getScore();
        int scoreRevision = game.getScoreRevision();
        return new GameDetails(id, teams, score, scoreRevision);
    }
}
