package dev.scoreboard;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface ScoreboardTest {
    TeamPair MEXICO_CANADA = TeamPair.of("Mexico", "Canada");

    Scoreboard scoreboard();

    @Test
    default void shouldHaveEmptySummaryWhenNoGamesWereStarted() {
        Summary summary = scoreboard().getSummary();
        List<GameSummary> gamesInSummary = summary.games();

        assertThat(gamesInSummary).isEmpty();
    }

    @Test
    default void shouldShowStartedGameInSummary() {
        scoreboard().startGame(MEXICO_CANADA);

        List<TeamPair> teamsInSummary = findTeamsInSummary();

        assertThat(teamsInSummary).containsExactly(MEXICO_CANADA);
    }

    @Test
    default void shouldFindStartedGameById() {
        GameDetails startedGame = scoreboard().startGame(MEXICO_CANADA);
        GameId id = startedGame.id();

        GameDetails foundGame = scoreboard().getGame(id);
        TeamPair teamsOfFoundGame = foundGame.teams();

        assertThat(teamsOfFoundGame).isEqualTo(MEXICO_CANADA);
    }

    @Test
    default void shouldFindStartedGameByTeams() {
        GameDetails startedGame = scoreboard().startGame(MEXICO_CANADA);
        GameId id = startedGame.id();

        GameDetails foundGame = scoreboard().getGame(MEXICO_CANADA);
        GameId idOfFoundGame = foundGame.id();

        assertThat(idOfFoundGame).isEqualTo(id);
    }

    private List<TeamPair> findTeamsInSummary() {
        Summary summary = scoreboard().getSummary();
        List<GameSummary> gamesInSummary = summary.games();

        List<TeamPair> teamsInSummary = new ArrayList<>();
        for (GameSummary gameInSummary : gamesInSummary) {
            TeamPair teams = gameInSummary.teams();
            teamsInSummary.add(teams);
        }

        return teamsInSummary;
    }
}
