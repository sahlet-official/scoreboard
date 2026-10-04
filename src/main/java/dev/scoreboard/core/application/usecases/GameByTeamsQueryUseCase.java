package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.GameByTeamsQueryPort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.Optional;

public class GameByTeamsQueryUseCase implements GameByTeamsQueryPort {
    private final GameRepository gameRepository;

    public GameByTeamsQueryUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameDetails execute(TeamPair teams) {
        ensureTeamsAreNotNull(teams);

        Optional<Game> foundGame = gameRepository.findGame(teams);
        boolean gameIsMissing = foundGame.isEmpty();
        if (gameIsMissing) {
            throw new GameNotFoundException(teams);
        }

        GameId placeholderId = new GameId(0);
        Score placeholderScore = new Score(0, 0);
        return new GameDetails(placeholderId, teams, placeholderScore, 0);
    }

    private static void ensureTeamsAreNotNull(TeamPair teams) {
        if (teams == null) {
            throw new NullPointerException("Teams must not be null");
        }
    }
}
