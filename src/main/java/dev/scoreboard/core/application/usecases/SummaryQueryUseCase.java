package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.SummaryQueryPort;
import dev.scoreboard.core.application.ports.inbound.models.GameSummary;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.usecases.mappers.GameSummaryMapper;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.Score;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SummaryQueryUseCase implements SummaryQueryPort {
    private static final Comparator<Game> BY_TOTAL_SCORE_HIGHEST_FIRST =
        Comparator.comparingInt(SummaryQueryUseCase::calculateTotalScore).reversed();

    private static final Comparator<Game> BY_MOST_RECENTLY_STARTED_FIRST =
        Comparator.comparingLong(Game::getSequenceNumber).reversed();

    private static final Comparator<Game> SUMMARY_ORDER =
        BY_TOTAL_SCORE_HIGHEST_FIRST.thenComparing(BY_MOST_RECENTLY_STARTED_FIRST);

    private final GameRepository gameRepository;

    public SummaryQueryUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Summary execute() {
        List<Game> games = gameRepository.findAllGames();
        List<Game> orderedGames = orderGamesForSummary(games);
        List<GameSummary> gameSummaries = createGameSummaries(orderedGames);
        return new Summary(gameSummaries);
    }

    private static List<Game> orderGamesForSummary(List<Game> games) {
        List<Game> orderedGames = new ArrayList<>(games);
        orderedGames.sort(SUMMARY_ORDER);
        return orderedGames;
    }

    private static int calculateTotalScore(Game game) {
        Score score = game.getScore();
        int homeScore = score.home();
        int awayScore = score.away();
        return homeScore + awayScore;
    }

    private static List<GameSummary> createGameSummaries(List<Game> games) {
        List<GameSummary> gameSummaries = new ArrayList<>();
        for (Game game : games) {
            GameSummary gameSummary = GameSummaryMapper.createGameSummary(game);
            gameSummaries.add(gameSummary);
        }
        return gameSummaries;
    }
}
