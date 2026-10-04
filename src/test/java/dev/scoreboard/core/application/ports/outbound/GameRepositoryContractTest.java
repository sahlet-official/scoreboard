package dev.scoreboard.core.application.ports.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.domain.entities.Game;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class GameRepositoryContractTest {
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
}
