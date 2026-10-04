package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameAlreadyInProgressException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;

class GameStartUseCaseTest {
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");

    private final GameStartRepositoryStub gameStartRepositoryStub = new GameStartRepositoryStub();
    private final GameStartUseCase gameStartUseCase = new GameStartUseCase(gameStartRepositoryStub);

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
        Game sameGame = gameInProgress(TEAMS);
        gameStartRepositoryStub.setGameInProgress(sameGame);

        Throwable failure = catchThrowable(() -> gameStartUseCase.execute(TEAMS));

        assertThat(failure).isInstanceOf(GameAlreadyInProgressException.class);
    }

    @Test
    void shouldReportTeamsAndIdOfGameThatIsAlreadyInProgress() {
        Game sameGame = gameInProgress(TEAMS);
        GameId idOfSameGame = sameGame.getId();
        gameStartRepositoryStub.setGameInProgress(sameGame);

        GameAlreadyInProgressException exception = catchThrowableOfType(
            GameAlreadyInProgressException.class,
            () -> gameStartUseCase.execute(TEAMS)
        );
        boolean reportsTeams = exception.getTeams().equals(TEAMS);
        boolean reportsGameId = exception.getGameId().equals(idOfSameGame);
        boolean reportsTeamsAndGameId = reportsTeams && reportsGameId;

        assertThat(reportsTeamsAndGameId).isTrue();
    }

    private static Game gameInProgress(TeamPair teams) {
        GameId id = new GameId(3);
        long sequenceNumber = 3;
        Score score = new Score(2, 1);
        int scoreRevision = 3;
        return new Game(id, sequenceNumber, teams, score, scoreRevision);
    }
}
