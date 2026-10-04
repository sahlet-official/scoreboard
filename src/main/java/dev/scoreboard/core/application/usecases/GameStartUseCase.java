package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.GameStartPort;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public class GameStartUseCase implements GameStartPort {
    private static final Score INITIAL_SCORE = new Score(0, 0);
    private static final int INITIAL_SCORE_REVISION = 0;

    private final GameRepository gameRepository;

    public GameStartUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameDetails execute(TeamPair teams) {
        NewGame newGame = new NewGame(teams, INITIAL_SCORE, INITIAL_SCORE_REVISION);
        try {
            gameRepository.addGameWithUniqueTeams(newGame);
        } catch (TeamsNotUniqueException e) {
            throw new UnsupportedOperationException("Not implemented yet", e);
        }
        GameId placeholderId = new GameId(0);
        return new GameDetails(placeholderId, teams, INITIAL_SCORE, INITIAL_SCORE_REVISION);
    }
}
