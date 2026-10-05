package dev.scoreboard;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.infrastructure.adapters.outbound.InMemoryGameRepository;

public final class InMemoryGameRepositoryFactory {
    private InMemoryGameRepositoryFactory() {
    }

    public static GameRepository createInMemoryGameRepository() {
        return new InMemoryGameRepository();
    }
}
