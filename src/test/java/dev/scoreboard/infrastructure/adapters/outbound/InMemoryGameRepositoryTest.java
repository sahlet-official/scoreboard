package dev.scoreboard.infrastructure.adapters.outbound;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.GameRepositoryContractTest;

class InMemoryGameRepositoryTest implements GameRepositoryContractTest {
    private final GameRepository gameRepository = new InMemoryGameRepository();

    @Override
    public GameRepository gameRepository() {
        return gameRepository;
    }
}
