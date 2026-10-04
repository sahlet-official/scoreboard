package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.SummaryQueryPort;
import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import java.util.List;

public class SummaryQueryUseCase implements SummaryQueryPort {
    private final GameRepository gameRepository;

    public SummaryQueryUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Summary execute() {
        List<GameSummary> noGames = List.of();
        return new Summary(noGames);
    }
}
