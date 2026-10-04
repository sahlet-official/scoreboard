package dev.scoreboard.core.application.ports.outbound;

import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.ANOTHER_SCORE;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.NEW_GAME;
import static dev.scoreboard.core.application.ports.outbound.GameRepositoryTestData.NEXT_SCORE_REVISION;
import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public interface StoredGameIsolationContractTest {
    GameRepository gameRepository();

    @Test
    default void shouldKeepStoredScoreWhenAddedGameIsChanged() throws TeamsNotUniqueException {
        Game addedGame = gameRepository().addGameWithUniqueTeams(NEW_GAME);
        GameId id = addedGame.getId();

        addedGame.updateScore(ANOTHER_SCORE, NEXT_SCORE_REVISION);
        Optional<Game> foundGame = gameRepository().findGame(id);
        Optional<Score> storedScore = foundGame.map(Game::getScore);

        assertThat(storedScore).contains(NEW_GAME.score());
    }
}
