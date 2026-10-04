package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.GameStartPort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameAlreadyInProgressException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
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
            Game storedGame = gameRepository.addGameWithUniqueTeams(newGame);
            return detailsOf(storedGame);
        } catch (TeamsNotUniqueException exception) {
            Game conflictingGame = exception.getConflictingGame();
            GameId conflictingGameId = conflictingGame.getId();
            throw new GameAlreadyInProgressException(teams, conflictingGameId);
        }
    }

    private static GameDetails detailsOf(Game game) {
        GameId id = game.getId();
        TeamPair teams = game.getTeams();
        Score score = game.getScore();
        int scoreRevision = game.getScoreRevision();
        return new GameDetails(id, teams, score, scoreRevision);
    }
}
