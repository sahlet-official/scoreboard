package dev.scoreboard.core.application.ports.inbound.exceptions;

import dev.scoreboard.core.domain.valueobjects.GameId;

public class TeamAlreadyPlayingException extends ScoreboardException {
    private final String team;
    private final GameId gameId;

    public TeamAlreadyPlayingException(String team, GameId gameId) {
        super(messageFor(team, gameId));
        this.team = team;
        this.gameId = gameId;
    }

    public String getTeam() {
        return team;
    }

    public GameId getGameId() {
        return gameId;
    }

    private static String messageFor(String team, GameId gameId) {
        long id = gameId.value();
        return "Team already playing: " + team + ", game id " + id;
    }
}
