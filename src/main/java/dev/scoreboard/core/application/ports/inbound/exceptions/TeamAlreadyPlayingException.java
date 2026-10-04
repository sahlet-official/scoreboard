package dev.scoreboard.core.application.ports.inbound.exceptions;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.TeamName;

public class TeamAlreadyPlayingException extends ScoreboardException {
    private final TeamName teamName;
    private final GameId gameId;

    public TeamAlreadyPlayingException(TeamName teamName, GameId gameId) {
        super(messageFor(teamName, gameId));
        this.teamName = teamName;
        this.gameId = gameId;
    }

    public TeamName getTeamName() {
        return teamName;
    }

    public GameId getGameId() {
        return gameId;
    }

    private static String messageFor(TeamName teamName, GameId gameId) {
        String nameOfTeam = teamName.value();
        long id = gameId.value();
        return "Team already playing: " + nameOfTeam + ", game id " + id;
    }
}
