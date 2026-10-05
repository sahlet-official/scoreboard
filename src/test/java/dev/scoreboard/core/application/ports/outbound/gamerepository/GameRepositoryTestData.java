package dev.scoreboard.core.application.ports.outbound.gamerepository;

import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public final class GameRepositoryTestData {
    public static final TeamPair MEXICO_CANADA = TeamPair.of("Mexico", "Canada");
    public static final TeamPair CANADA_MEXICO = TeamPair.of("Canada", "Mexico");
    public static final TeamPair MEXICO_BRAZIL = TeamPair.of("Mexico", "Brazil");
    public static final TeamPair SPAIN_CANADA = TeamPair.of("Spain", "Canada");
    public static final TeamPair SPAIN_BRAZIL = TeamPair.of("Spain", "Brazil");

    public static final Score SCORE = new Score(2, 1);
    public static final Score ANOTHER_SCORE = new Score(3, 1);
    public static final int SCORE_REVISION = 3;
    public static final int NEXT_SCORE_REVISION = SCORE_REVISION + 1;

    public static final NewGame NEW_GAME = new NewGame(MEXICO_CANADA, SCORE, SCORE_REVISION);
    public static final NewGame ANOTHER_NEW_GAME = new NewGame(SPAIN_BRAZIL, SCORE, SCORE_REVISION);
    public static final NewGame NEW_GAME_WITH_SAME_HOME_TEAM = new NewGame(MEXICO_BRAZIL, SCORE, SCORE_REVISION);
    public static final NewGame NEW_GAME_WITH_SAME_AWAY_TEAM = new NewGame(SPAIN_CANADA, SCORE, SCORE_REVISION);
    public static final NewGame NEW_GAME_WITH_SAME_TEAMS_IN_REVERSE_ORDER = new NewGame(
        CANADA_MEXICO, SCORE, SCORE_REVISION
    );

    public static final GameId UNKNOWN_GAME_ID = new GameId(404);

    private GameRepositoryTestData() {
    }
}
