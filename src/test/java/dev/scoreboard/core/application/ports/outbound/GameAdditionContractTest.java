package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public interface GameAdditionContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldHaveNoGamesWhenNothingWasAdded() {
        List<Game> games = gameRepository().findAllGames();

        assertThat(games).isEmpty();
    }

    @Test
    default void shouldReturnAddedGameWithGivenTeamsScoreAndScoreRevision() throws TeamsNotUniqueException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);

        boolean keepsTeams = addedGame.getTeams().equals(NEW_GAME.teams());
        boolean keepsScore = addedGame.getScore().equals(NEW_GAME.score());
        boolean keepsScoreRevision = addedGame.getScoreRevision() == NEW_GAME.scoreRevision();
        boolean keepsGivenValues = keepsTeams && keepsScore && keepsScoreRevision;

        assertThat(keepsGivenValues).isTrue();
    }

    @Test
    default void shouldAssignDifferentIdsToAddedGames() throws TeamsNotUniqueException {
        Game firstGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        Game secondGame = gameRepository().addGameWithUniqueTeams(ANOTHER_NEW_GAME);

        GameId idOfFirstGame = firstGame.getId();
        GameId idOfSecondGame = secondGame.getId();

        assertThat(idOfSecondGame).isNotEqualTo(idOfFirstGame);
    }

    @Test
    default void shouldAssignGreaterSequenceNumberToGameAddedLater() throws TeamsNotUniqueException {
        Game firstGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        Game secondGame = gameRepository().addGameWithUniqueTeams(ANOTHER_NEW_GAME);

        long sequenceNumberOfFirstGame = firstGame.getSequenceNumber();
        long sequenceNumberOfSecondGame = secondGame.getSequenceNumber();

        assertThat(sequenceNumberOfSecondGame).isGreaterThan(sequenceNumberOfFirstGame);
    }

    @Test
    default void shouldRejectGameWhenItsHomeTeamIsPlayingInAnotherGame() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Throwable failure = catchThrowable(
            () -> gameRepository().addGameWithUniqueTeams(NEW_GAME_WITH_SAME_HOME_TEAM)
        );

        assertThat(failure).isInstanceOf(TeamsNotUniqueException.class);
    }

    @Test
    default void shouldRejectGameWhenItsAwayTeamIsPlayingInAnotherGame() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Throwable failure = catchThrowable(
            () -> gameRepository().addGameWithUniqueTeams(NEW_GAME_WITH_SAME_AWAY_TEAM)
        );

        assertThat(failure).isInstanceOf(TeamsNotUniqueException.class);
    }

    @Test
    default void shouldRejectGameWithSameTeamsInReverseOrder() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Throwable failure = catchThrowable(
            () -> gameRepository().addGameWithUniqueTeams(NEW_GAME_WITH_SAME_TEAMS_IN_REVERSE_ORDER)
        );

        assertThat(failure).isInstanceOf(TeamsNotUniqueException.class);
    }

    @Test
    default void shouldReportGameWhereTeamIsPlayingWhenGameIsRejected() throws TeamsNotUniqueException {
        Game gameInProgress = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        GameId idOfGameInProgress = gameInProgress.getId();

        TeamsNotUniqueException exception = catchThrowableOfType(
            TeamsNotUniqueException.class,
            () -> gameRepository().addGameWithUniqueTeams(NEW_GAME_WITH_SAME_HOME_TEAM)
        );
        Game conflictingGame = exception.getConflictingGame();
        GameId idOfConflictingGame = conflictingGame.getId();

        assertThat(idOfConflictingGame).isEqualTo(idOfGameInProgress);
    }

    @Test
    default void shouldNotStoreRejectedGame() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);
        catchThrowable(() -> gameRepository().addGameWithUniqueTeams(NEW_GAME_WITH_SAME_AWAY_TEAM));

        List<Game> games = gameRepository().findAllGames();

        assertThat(games).extracting(Game::getTeams).containsExactly(NEW_GAME.teams());
    }

    @Test
    default void shouldFindAddedGameById() throws TeamsNotUniqueException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        GameId id = addedGame.getId();

        Optional<Game> foundGame = gameRepository().findGame(id);
        Optional<TeamPair> teamsOfFoundGame = foundGame.map(Game::getTeams);

        assertThat(teamsOfFoundGame).contains(NEW_GAME.teams());
    }

    @Test
    default void shouldFindNothingByIdOfGameThatWasNotAdded() {
        Optional<Game> foundGame = gameRepository().findGame(UNKNOWN_GAME_ID);

        assertThat(foundGame).isEmpty();
    }

    @Test
    default void shouldFindAddedGameByTeams() throws TeamsNotUniqueException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        GameId id = addedGame.getId();

        Optional<Game> foundGame = gameRepository().findGame(NEW_GAME.teams());
        Optional<GameId> idOfFoundGame = foundGame.map(Game::getId);

        assertThat(idOfFoundGame).contains(id);
    }

    @Test
    default void shouldFindNothingByTeamsOfGameThatWasNotAdded() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Optional<Game> foundGame = gameRepository().findGame(ANOTHER_NEW_GAME.teams());

        assertThat(foundGame).isEmpty();
    }

    @Test
    default void shouldFindNothingByTeamsOfAddedGameInReverseOrder() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Optional<Game> foundGame = gameRepository().findGame(CANADA_MEXICO);

        assertThat(foundGame).isEmpty();
    }

    @Test
    default void shouldFindNothingByTeamsThatShareOnlyHomeTeamWithAddedGame() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Optional<Game> foundGame = gameRepository().findGame(NEW_GAME_WITH_SAME_HOME_TEAM.teams());

        assertThat(foundGame).isEmpty();
    }

    @Test
    default void shouldFindNothingByTeamsThatShareOnlyAwayTeamWithAddedGame() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);

        Optional<Game> foundGame = gameRepository().findGame(NEW_GAME_WITH_SAME_AWAY_TEAM.teams());

        assertThat(foundGame).isEmpty();
    }

    @Test
    default void shouldListAllAddedGames() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);
        gameRepository().addGameWithUniqueTeams(ANOTHER_NEW_GAME);

        List<Game> games = gameRepository().findAllGames();

        assertThat(games).extracting(Game::getTeams)
            .containsExactlyInAnyOrder(NEW_GAME.teams(), ANOTHER_NEW_GAME.teams());
    }
}
