package dev.scoreboard.core.application.ports.inbound.exceptions;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public class GameAlreadyInProgressException extends ScoreboardException {
    private final TeamPair teams;
    private final GameId gameId;

    public GameAlreadyInProgressException(TeamPair teams, GameId gameId) {
        super(messageFor(teams, gameId));
        this.teams = teams;
        this.gameId = gameId;
    }

    public TeamPair getTeams() {
        return teams;
    }

    public GameId getGameId() {
        return gameId;
    }

    private static String messageFor(TeamPair teams, GameId gameId) {
        String game = teams.homeTeam() + " - " + teams.awayTeam();
        long id = gameId.value();
        return "Game already in progress: " + game + ", id " + id;
    }
}
