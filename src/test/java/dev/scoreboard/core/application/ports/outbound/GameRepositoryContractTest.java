package dev.scoreboard.core.application.ports.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class GameRepositoryContractTest {
    private static final TeamPair MEXICO_CANADA = TeamPair.of("Mexico", "Canada");
    private static final TeamPair SPAIN_BRAZIL = TeamPair.of("Spain", "Brazil");
    private static final Score SCORE = new Score(2, 1);
    private static final int SCORE_REVISION = 3;

    private static final NewGame NEW_GAME = new NewGame(MEXICO_CANADA, SCORE, SCORE_REVISION);
    private static final NewGame ANOTHER_NEW_GAME = new NewGame(SPAIN_BRAZIL, SCORE, SCORE_REVISION);

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

    @Test
    void shouldFindAddedGameById() throws TeamsNotUniqueException {
        Game addedGame = gameRepository.addGameWithUniqueTeams(NEW_GAME);
        GameId id = addedGame.getId();

        Optional<Game> foundGame = gameRepository.findGame(id);
        Optional<TeamPair> teamsOfFoundGame = foundGame.map(Game::getTeams);

        assertThat(teamsOfFoundGame).contains(MEXICO_CANADA);
    }

    @Test
    void shouldFindNothingByIdOfGameThatWasNotAdded() {
        GameId unknownId = new GameId(404);

        Optional<Game> foundGame = gameRepository.findGame(unknownId);

        assertThat(foundGame).isEmpty();
    }

    @Test
    void shouldFindAddedGameByTeams() throws TeamsNotUniqueException {
        Game addedGame = gameRepository.addGameWithUniqueTeams(NEW_GAME);
        GameId id = addedGame.getId();

        Optional<Game> foundGame = gameRepository.findGame(MEXICO_CANADA);
        Optional<GameId> idOfFoundGame = foundGame.map(Game::getId);

        assertThat(idOfFoundGame).contains(id);
    }

    @Test
    void shouldFindNothingByTeamsOfGameThatWasNotAdded() throws TeamsNotUniqueException {
        gameRepository.addGameWithUniqueTeams(NEW_GAME);

        Optional<Game> foundGame = gameRepository.findGame(SPAIN_BRAZIL);

        assertThat(foundGame).isEmpty();
    }

    @Test
    void shouldListAllAddedGames() throws TeamsNotUniqueException {
        gameRepository.addGameWithUniqueTeams(NEW_GAME);
        gameRepository.addGameWithUniqueTeams(ANOTHER_NEW_GAME);

        List<Game> games = gameRepository.findAllGames();

        assertThat(games).extracting(Game::getTeams).containsExactlyInAnyOrder(MEXICO_CANADA, SPAIN_BRAZIL);
    }
}
