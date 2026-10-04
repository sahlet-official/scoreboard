package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;

class GameStartUseCaseTest {
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");

    private final GameRepositoryStub gameRepositoryStub = new GameRepositoryStub();
    private final GameStartUseCase gameStartUseCase = new GameStartUseCase(gameRepositoryStub);

    @Test
    void shouldAddGameWithZeroScoreToRepository() {
        Score zeroScore = new Score(0, 0);

        gameStartUseCase.execute(TEAMS);
        NewGame addedGame = gameRepositoryStub.getAddedGame();
        Score score = addedGame.score();

        assertThat(score).isEqualTo(zeroScore);
    }

    @Test
    void shouldAddGameWithZeroScoreRevisionToRepository() {
        int zeroRevision = 0;

        gameStartUseCase.execute(TEAMS);
        NewGame addedGame = gameRepositoryStub.getAddedGame();
        int scoreRevision = addedGame.scoreRevision();

        assertThat(scoreRevision).isEqualTo(zeroRevision);
    }

    @Test
    void shouldAddGameWithGivenTeamsToRepository() {
        gameStartUseCase.execute(TEAMS);
        NewGame addedGame = gameRepositoryStub.getAddedGame();
        TeamPair teams = addedGame.teams();

        assertThat(teams).isEqualTo(TEAMS);
    }

    @Test
    void shouldReturnGameWithIdAssignedByRepository() {
        GameDetails game = gameStartUseCase.execute(TEAMS);
        GameId id = game.id();

        assertThat(id).isEqualTo(GameRepositoryStub.ASSIGNED_ID);
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
}
