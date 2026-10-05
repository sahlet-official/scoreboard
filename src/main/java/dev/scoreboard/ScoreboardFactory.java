package dev.scoreboard;

import dev.scoreboard.core.application.ports.inbound.SummaryQueryPort;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.usecases.SummaryQueryUseCase;

public final class ScoreboardFactory {
    private ScoreboardFactory() {
    }

    public static Scoreboard createInMemoryScoreboard() {
        GameRepository gameRepository = InMemoryGameRepositoryFactory.createInMemoryGameRepository();

        return createScoreboard(gameRepository);
    }

    public static Scoreboard createScoreboard(GameRepository gameRepository) {
        SummaryQueryPort summaryQueryPort = new SummaryQueryUseCase(gameRepository);

        return new ScoreboardImpl(
            null,
            null,
            null,
            null,
            null,
            summaryQueryPort
        );
    }
}
