package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.usecases.stubs.SummaryQueryRepositoryStub;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SummaryQueryUseCaseTest {
    private static final TeamPair MEXICO_CANADA = TeamPair.of("Mexico", "Canada");
    private static final TeamPair SPAIN_BRAZIL = TeamPair.of("Spain", "Brazil");
    private static final TeamPair GERMANY_FRANCE = TeamPair.of("Germany", "France");

    private final SummaryQueryRepositoryStub summaryQueryRepositoryStub = new SummaryQueryRepositoryStub();
    private final SummaryQueryUseCase summaryQueryUseCase = new SummaryQueryUseCase(summaryQueryRepositoryStub);

    private long nextSequenceNumber = 1;

    @Test
    void shouldReturnEmptySummaryWhenNoGamesAreInProgress() {
        List<GameSummary> games = gamesInSummary();

        assertThat(games).isEmpty();
    }

    @Test
    void shouldReturnTeamsAndScoreOfGameInProgress() {
        gamesInProgress(
            game(MEXICO_CANADA, 0, 5)
        );

        List<GameSummary> games = gamesInSummary();

        assertThat(games).containsExactly(gameSummary(MEXICO_CANADA, 0, 5));
    }

    @Test
    void shouldOrderGamesByTotalScoreHighestFirst() {
        gamesInProgress(
            game(MEXICO_CANADA, 0, 1),
            game(SPAIN_BRAZIL, 3, 2),
            game(GERMANY_FRANCE, 2, 0)
        );

        List<TeamPair> teamsInOrder = teamsInSummary();

        assertThat(teamsInOrder).containsExactly(SPAIN_BRAZIL, GERMANY_FRANCE, MEXICO_CANADA);
    }

    @Test
    void shouldOrderGamesWithSameTotalScoreByMostRecentlyStartedFirst() {
        gamesInProgress(
            game(MEXICO_CANADA, 2, 2),
            game(SPAIN_BRAZIL, 4, 0),
            game(GERMANY_FRANCE, 1, 3)
        );

        List<TeamPair> teamsInOrder = teamsInSummary();

        assertThat(teamsInOrder).containsExactly(GERMANY_FRANCE, SPAIN_BRAZIL, MEXICO_CANADA);
    }

    private void gamesInProgress(Game... games) {
        List<Game> gamesInProgress = List.of(games);
        summaryQueryRepositoryStub.setGames(gamesInProgress);
    }

    private Game game(TeamPair teams, int homeScore, int awayScore) {
        long sequenceNumber = nextSequenceNumber;
        nextSequenceNumber++;

        GameId id = new GameId(sequenceNumber);
        Score score = new Score(homeScore, awayScore);
        int scoreRevision = 0;
        return new Game(id, sequenceNumber, teams, score, scoreRevision);
    }

    private static GameSummary gameSummary(TeamPair teams, int homeScore, int awayScore) {
        Score score = new Score(homeScore, awayScore);
        return new GameSummary(teams, score);
    }

    private List<GameSummary> gamesInSummary() {
        Summary summary = summaryQueryUseCase.execute();
        return summary.games();
    }

    private List<TeamPair> teamsInSummary() {
        List<GameSummary> games = gamesInSummary();

        List<TeamPair> teams = new ArrayList<>();
        for (GameSummary game : games) {
            TeamPair teamsOfGame = game.teams();
            teams.add(teamsOfGame);
        }

        return teams;
    }
}
