package dev.scoreboard.infrastructure.adapters.outbound;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.GameRepositoryContractTest;

class InMemoryGameRepositoryTest extends GameRepositoryContractTest {
    @Override
    protected GameRepository createRepository() {
        return new InMemoryGameRepository();
    }
}
