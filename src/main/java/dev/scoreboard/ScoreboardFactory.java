package dev.scoreboard;

import dev.scoreboard.core.application.ports.inbound.GameByTeamsQueryPort;
import dev.scoreboard.core.application.ports.inbound.GameQueryPort;
import dev.scoreboard.core.application.ports.inbound.GameStartPort;
import dev.scoreboard.core.application.ports.inbound.ScoreUpdatePort;
import dev.scoreboard.core.application.ports.inbound.SummaryQueryPort;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.usecases.GameByTeamsQueryUseCase;
import dev.scoreboard.core.application.usecases.GameQueryUseCase;
import dev.scoreboard.core.application.usecases.GameStartUseCase;
import dev.scoreboard.core.application.usecases.ScoreUpdateUseCase;
import dev.scoreboard.core.application.usecases.SummaryQueryUseCase;

public final class ScoreboardFactory {
    private ScoreboardFactory() {
    }

    public static Scoreboard createInMemoryScoreboard() {
        GameRepository gameRepository = InMemoryGameRepositoryFactory.createInMemoryGameRepository();

        return createScoreboard(gameRepository);
    }

    public static Scoreboard createScoreboard(GameRepository gameRepository) {
        GameStartPort gameStartPort = new GameStartUseCase(gameRepository);
        ScoreUpdatePort scoreUpdatePort = new ScoreUpdateUseCase(gameRepository);
        GameQueryPort gameQueryPort = new GameQueryUseCase(gameRepository);
        GameByTeamsQueryPort gameByTeamsQueryPort = new GameByTeamsQueryUseCase(gameRepository);
        SummaryQueryPort summaryQueryPort = new SummaryQueryUseCase(gameRepository);

        return new ScoreboardImpl(
            gameStartPort,
            scoreUpdatePort,
            null,
            gameQueryPort,
            gameByTeamsQueryPort,
            summaryQueryPort
        );
    }
}
