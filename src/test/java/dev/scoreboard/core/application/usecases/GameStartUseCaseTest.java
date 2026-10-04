package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameAlreadyInProgressException;
import dev.scoreboard.core.application.ports.inbound.exceptions.TeamAlreadyPlayingException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.application.usecases.stubs.GameStartRepositoryStub;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamName;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;

class GameStartUseCaseTest {
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");
    private static final TeamName HOME_TEAM_NAME = TEAMS.homeTeam();
    private static final TeamName AWAY_TEAM_NAME = TEAMS.awayTeam();
    private static final TeamPair ANOTHER_TEAMS = TeamPair.of("Spain", "Germany");
    private static final TeamName ANOTHER_TEAM_NAME = new TeamName("Brazil");

    private static final GameId ID_OF_GAME_IN_PROGRESS = new GameId(3);
    private static final Game SAME_GAME = gameInProgress(TEAMS);

    private static final Game ANOTHER_GAME_WITH_HOME_TEAM = gameInProgress(
        new TeamPair(HOME_TEAM_NAME, ANOTHER_TEAM_NAME)
    );
    private static final Game ANOTHER_GAME_WITH_AWAY_TEAM = gameInProgress(
        new TeamPair(ANOTHER_TEAM_NAME, AWAY_TEAM_NAME)
    );
    private static final Game UNRELATED_GAME = gameInProgress(ANOTHER_TEAMS);

    private final GameStartRepositoryStub gameStartRepositoryStub = new GameStartRepositoryStub();
    private final GameStartUseCase gameStartUseCase = new GameStartUseCase(gameStartRepositoryStub);

    @Test
    void shouldRejectMissingTeams() {
        TeamPair missingTeams = null;

        Throwable failure = catchThrowable(() -> gameStartUseCase.execute(missingTeams));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldAddGameWithZeroScoreToRepository() {
        Score zeroScore = new Score(0, 0);

        gameStartUseCase.execute(TEAMS);
        NewGame addedGame = gameStartRepositoryStub.getAddedGame();
        Score score = addedGame.score();

        assertThat(score).isEqualTo(zeroScore);
    }

    @Test
    void shouldAddGameWithZeroScoreRevisionToRepository() {
        int zeroRevision = 0;

        gameStartUseCase.execute(TEAMS);
        NewGame addedGame = gameStartRepositoryStub.getAddedGame();
        int scoreRevision = addedGame.scoreRevision();

        assertThat(scoreRevision).isEqualTo(zeroRevision);
    }

    @Test
    void shouldAddGameWithGivenTeamsToRepository() {
        gameStartUseCase.execute(TEAMS);
        NewGame addedGame = gameStartRepositoryStub.getAddedGame();
        TeamPair teams = addedGame.teams();

        assertThat(teams).isEqualTo(TEAMS);
    }

    @Test
    void shouldReturnGameWithIdAssignedByRepository() {
        GameDetails game = gameStartUseCase.execute(TEAMS);
        GameId id = game.id();

        assertThat(id).isEqualTo(GameStartRepositoryStub.ASSIGNED_ID);
    }

    @Test
    void shouldReturnGameWithGivenTeams() {
        GameDetails game = gameStartUseCase.execute(TEAMS);
        TeamPair teams = game.teams();

        assertThat(teams).isEqualTo(TEAMS);
    }

    @Test
    void shouldReturnGameWithZeroScore() {
        Score zeroScore = new Score(0, 0);

        GameDetails game = gameStartUseCase.execute(TEAMS);
        Score score = game.score();

        assertThat(score).isEqualTo(zeroScore);
    }

    @Test
    void shouldReturnGameWithZeroScoreRevision() {
        int zeroRevision = 0;

        GameDetails game = gameStartUseCase.execute(TEAMS);
        int scoreRevision = game.scoreRevision();

        assertThat(scoreRevision).isEqualTo(zeroRevision);
    }

    @Test
    void shouldRejectStartWhenSameGameIsAlreadyInProgress() {
        gameStartRepositoryStub.setGameInProgress(SAME_GAME);

        Throwable failure = catchThrowable(() -> gameStartUseCase.execute(TEAMS));

        assertThat(failure).isInstanceOf(GameAlreadyInProgressException.class);
    }

    @Test
    void shouldReportTeamsAndIdOfGameThatIsAlreadyInProgress() {
        gameStartRepositoryStub.setGameInProgress(SAME_GAME);

        GameAlreadyInProgressException exception = catchThrowableOfType(
            GameAlreadyInProgressException.class,
            () -> gameStartUseCase.execute(TEAMS)
        );
        boolean reportsTeams = exception.getTeams().equals(TEAMS);
        boolean reportsGameId = exception.getGameId().equals(ID_OF_GAME_IN_PROGRESS);
        boolean reportsTeamsAndGameId = reportsTeams && reportsGameId;

        assertThat(reportsTeamsAndGameId).isTrue();
    }

    @Test
    void shouldRejectStartWhenHomeTeamIsPlayingInAnotherGame() {
        gameStartRepositoryStub.setGameInProgress(ANOTHER_GAME_WITH_HOME_TEAM);

        Throwable failure = catchThrowable(() -> gameStartUseCase.execute(TEAMS));

        assertThat(failure).isInstanceOf(TeamAlreadyPlayingException.class);
    }

    @Test
    void shouldReportHomeTeamAndIdOfAnotherGameWhereItIsPlaying() {
        gameStartRepositoryStub.setGameInProgress(ANOTHER_GAME_WITH_HOME_TEAM);

        TeamAlreadyPlayingException exception = catchThrowableOfType(
            TeamAlreadyPlayingException.class,
            () -> gameStartUseCase.execute(TEAMS)
        );
        boolean reportsTeamName = exception.getTeamName().equals(HOME_TEAM_NAME);
        boolean reportsGameId = exception.getGameId().equals(ID_OF_GAME_IN_PROGRESS);
        boolean reportsTeamNameAndGameId = reportsTeamName && reportsGameId;

        assertThat(reportsTeamNameAndGameId).isTrue();
    }

    @Test
    void shouldRejectStartWhenAwayTeamIsPlayingInAnotherGame() {
        gameStartRepositoryStub.setGameInProgress(ANOTHER_GAME_WITH_AWAY_TEAM);

        Throwable failure = catchThrowable(() -> gameStartUseCase.execute(TEAMS));

        assertThat(failure).isInstanceOf(TeamAlreadyPlayingException.class);
    }

    @Test
    void shouldReportAwayTeamAndIdOfAnotherGameWhereItIsPlaying() {
        gameStartRepositoryStub.setGameInProgress(ANOTHER_GAME_WITH_AWAY_TEAM);

        TeamAlreadyPlayingException exception = catchThrowableOfType(
            TeamAlreadyPlayingException.class,
            () -> gameStartUseCase.execute(TEAMS)
        );
        boolean reportsTeamName = exception.getTeamName().equals(AWAY_TEAM_NAME);
        boolean reportsGameId = exception.getGameId().equals(ID_OF_GAME_IN_PROGRESS);
        boolean reportsTeamNameAndGameId = reportsTeamName && reportsGameId;

        assertThat(reportsTeamNameAndGameId).isTrue();
    }

    @Test
    void shouldFailWhenRepositoryReportsConflictWithUnrelatedGame() {
        gameStartRepositoryStub.setGameWronglyReportedAsConflicting(UNRELATED_GAME);

        Throwable failure = catchThrowable(() -> gameStartUseCase.execute(TEAMS));

        assertThat(failure).isInstanceOf(IllegalStateException.class);
    }

    private static Game gameInProgress(TeamPair teams) {
        long sequenceNumber = ID_OF_GAME_IN_PROGRESS.value();
        Score score = new Score(2, 1);
        int scoreRevision = 3;
        return new Game(ID_OF_GAME_IN_PROGRESS, sequenceNumber, teams, score, scoreRevision);
    }
}
