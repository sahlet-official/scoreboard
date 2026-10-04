package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.SummaryQueryPort;
import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.usecases.mappers.GameSummaryMapper;
import dev.scoreboard.core.domain.entities.Game;
import java.util.ArrayList;
import java.util.List;

public class SummaryQueryUseCase implements SummaryQueryPort {
    private final GameRepository gameRepository;

    public SummaryQueryUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Summary execute() {
        List<Game> games = gameRepository.findAllGames();

        List<GameSummary> gameSummaries = new ArrayList<>();
        for (Game game : games) {
            GameSummary gameSummary = GameSummaryMapper.createGameSummary(game);
            gameSummaries.add(gameSummary);
        }

        return new Summary(gameSummaries);
    }
}
