package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.GameQueryPort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.Optional;

public class GameQueryUseCase implements GameQueryPort {
    private final GameRepository gameRepository;

    public GameQueryUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameDetails execute(GameId gameId) {
        ensureGameIdIsNotNull(gameId);

        Optional<Game> foundGame = gameRepository.findGame(gameId);
        boolean gameIsMissing = foundGame.isEmpty();
        if (gameIsMissing) {
            throw new GameNotFoundException(gameId);
        }

        TeamPair placeholderTeams = TeamPair.of("Home", "Away");
        Score placeholderScore = new Score(0, 0);
        return new GameDetails(gameId, placeholderTeams, placeholderScore, 0);
    }

    private static void ensureGameIdIsNotNull(GameId gameId) {
        if (gameId == null) {
            throw new NullPointerException("Game ID must not be null");
        }
    }
}
