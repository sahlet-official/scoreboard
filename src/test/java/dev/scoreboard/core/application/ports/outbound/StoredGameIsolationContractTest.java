package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.Score;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public interface StoredGameIsolationContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldKeepStoredScoreWhenAddedGameIsChanged() throws TeamsNotUniqueException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);

        changeScore(addedGame);
        Score storedScore = findStoredScore();

        assertThat(storedScore).isEqualTo(NEW_GAME.score());
    }

    @Test
    default void shouldKeepStoredScoreWhenGameFoundByIdIsChanged() throws TeamsNotUniqueException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        Game gameFoundById = gameRepository().findGame(addedGame.getId()).orElseThrow();

        changeScore(gameFoundById);
        Score storedScore = findStoredScore();

        assertThat(storedScore).isEqualTo(NEW_GAME.score());
    }

    @Test
    default void shouldKeepStoredScoreWhenGameFoundByTeamsIsChanged() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);
        Game gameFoundByTeams = gameRepository().findGame(NEW_GAME.teams()).orElseThrow();

        changeScore(gameFoundByTeams);
        Score storedScore = findStoredScore();

        assertThat(storedScore).isEqualTo(NEW_GAME.score());
    }

    @Test
    default void shouldKeepStoredScoreWhenGameFromListOfAllGamesIsChanged() throws TeamsNotUniqueException {
        gameRepository().addGameWithUniqueTeams(NEW_GAME);
        Game gameFromList = gameRepository().findAllGames().get(0);

        changeScore(gameFromList);
        Score storedScore = findStoredScore();

        assertThat(storedScore).isEqualTo(NEW_GAME.score());
    }

    private static void changeScore(Game game) {
        game.updateScore(ANOTHER_SCORE, NEXT_SCORE_REVISION);
    }

    private Score findStoredScore() {
        Optional<Game> foundGame = gameRepository().findGame(NEW_GAME.teams());
        Game storedGame = foundGame.orElseThrow();
        return storedGame.getScore();
    }
}
