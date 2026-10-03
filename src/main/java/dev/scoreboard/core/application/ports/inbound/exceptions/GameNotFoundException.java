package dev.scoreboard.core.application.ports.inbound.exceptions;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.Optional;

public class GameNotFoundException extends ScoreboardException {
    private final GameId gameId;
    private final TeamPair teams;

    public GameNotFoundException(GameId gameId) {
        super(messageFor(gameId));
        this.gameId = gameId;
        this.teams = null;
    }

    public GameNotFoundException(TeamPair teams) {
        super(messageFor(teams));
        this.gameId = null;
        this.teams = teams;
    }

    public Optional<GameId> getGameId() {
        return Optional.ofNullable(gameId);
    }

    public Optional<TeamPair> getTeams() {
        return Optional.ofNullable(teams);
    }

    private static String messageFor(GameId gameId) {
        long id = gameId.value();
        return "Game not found: id " + id;
    }

    private static String messageFor(TeamPair teams) {
        String game = teams.homeTeam() + " - " + teams.awayTeam();
        return "Game not found: " + game;
    }
}
