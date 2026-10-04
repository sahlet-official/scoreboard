package dev.scoreboard.core.application.ports.outbound;

import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

final class GameRepositoryTestData {
    static final TeamPair MEXICO_CANADA = TeamPair.of("Mexico", "Canada");
    static final TeamPair CANADA_MEXICO = TeamPair.of("Canada", "Mexico");
    static final TeamPair MEXICO_BRAZIL = TeamPair.of("Mexico", "Brazil");
    static final TeamPair SPAIN_CANADA = TeamPair.of("Spain", "Canada");
    static final TeamPair SPAIN_BRAZIL = TeamPair.of("Spain", "Brazil");

    static final Score SCORE = new Score(2, 1);
    static final Score ANOTHER_SCORE = new Score(3, 1);
    static final int SCORE_REVISION = 3;
    static final int NEXT_SCORE_REVISION = SCORE_REVISION + 1;

    static final NewGame NEW_GAME = new NewGame(MEXICO_CANADA, SCORE, SCORE_REVISION);
    static final NewGame ANOTHER_NEW_GAME = new NewGame(SPAIN_BRAZIL, SCORE, SCORE_REVISION);
    static final NewGame NEW_GAME_WITH_SAME_HOME_TEAM = new NewGame(MEXICO_BRAZIL, SCORE, SCORE_REVISION);
    static final NewGame NEW_GAME_WITH_SAME_AWAY_TEAM = new NewGame(SPAIN_CANADA, SCORE, SCORE_REVISION);

    static final GameId UNKNOWN_GAME_ID = new GameId(404);

    private GameRepositoryTestData() {
    }
}
