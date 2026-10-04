package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.inbound.GameStartPort;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameAlreadyInProgressException;
import dev.scoreboard.core.application.ports.inbound.exceptions.ScoreboardException;
import dev.scoreboard.core.application.ports.inbound.exceptions.TeamAlreadyPlayingException;
import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.application.usecases.mappers.GameDetailsMapper;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamName;
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
            return GameDetailsMapper.createGameDetails(storedGame);
        } catch (TeamsNotUniqueException exception) {
            Game conflictingGame = exception.getConflictingGame();
            throw createGameConflictException(teams, conflictingGame);
        }
    }

    private static ScoreboardException createGameConflictException(TeamPair teams, Game conflictingGame) {
        GameId conflictingGameId = conflictingGame.getId();
        TeamPair conflictingTeams = conflictingGame.getTeams();

        boolean sameGame = teams.equals(conflictingTeams);
        if (sameGame) {
            return new GameAlreadyInProgressException(teams, conflictingGameId);
        }

        TeamName busyTeamName = findCommonTeamName(teams, conflictingTeams);
        return new TeamAlreadyPlayingException(busyTeamName, conflictingGameId);
    }

    private static TeamName findCommonTeamName(TeamPair teams, TeamPair otherTeams) {
        TeamName homeTeam = teams.homeTeam();
        boolean homeTeamIsCommon = contains(otherTeams, homeTeam);
        if (homeTeamIsCommon) {
            return homeTeam;
        }

        TeamName awayTeam = teams.awayTeam();
        boolean awayTeamIsCommon = contains(otherTeams, awayTeam);
        if (awayTeamIsCommon) {
            return awayTeam;
        }

        throw new IllegalStateException("Team pairs have no common team");
    }

    private static boolean contains(TeamPair teams, TeamName teamName) {
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();
        boolean isHomeTeam = teamName.equals(homeTeam);
        boolean isAwayTeam = teamName.equals(awayTeam);
        return isHomeTeam || isAwayTeam;
    }
}
