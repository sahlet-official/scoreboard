package dev.scoreboard.core.application.ports.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class GameRepositoryContractTest {
    private static final TeamPair MEXICO_CANADA = TeamPair.of("Mexico", "Canada");
    private static final Score SCORE = new Score(2, 1);
    private static final int SCORE_REVISION = 3;
    private static final NewGame NEW_GAME = new NewGame(MEXICO_CANADA, SCORE, SCORE_REVISION);

    private GameRepository gameRepository;

    protected abstract GameRepository createRepository();

    @BeforeEach
    void setUpRepository() {
        gameRepository = createRepository();
    }

    @Test
    void shouldHaveNoGamesWhenNothingWasAdded() {
        List<Game> games = gameRepository.findAllGames();

        assertThat(games).isEmpty();
    }

    @Test
    void shouldReturnAddedGameWithGivenTeamsScoreAndScoreRevision() throws TeamsNotUniqueException {
        Game addedGame = gameRepository.addGameWithUniqueTeams(NEW_GAME);

        boolean keepsTeams = addedGame.getTeams().equals(MEXICO_CANADA);
        boolean keepsScore = addedGame.getScore().equals(SCORE);
        boolean keepsScoreRevision = addedGame.getScoreRevision() == SCORE_REVISION;
        boolean keepsGivenValues = keepsTeams && keepsScore && keepsScoreRevision;

        assertThat(keepsGivenValues).isTrue();
    }
}
