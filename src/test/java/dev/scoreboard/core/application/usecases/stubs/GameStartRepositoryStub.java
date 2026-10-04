package dev.scoreboard.core.application.usecases.stubs;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamName;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

public class GameStartRepositoryStub implements GameRepository {
    public static final GameId ASSIGNED_ID = new GameId(7);

    private Game gameInProgress;
    private Game gameWronglyReportedAsConflicting;
    private NewGame addedGame;

    public void setGameInProgress(Game gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public void setGameWronglyReportedAsConflicting(Game gameWronglyReportedAsConflicting) {
        this.gameWronglyReportedAsConflicting = gameWronglyReportedAsConflicting;
    }

    public NewGame getAddedGame() {
        return addedGame;
    }

    @Override
    public Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException {
        if (gameWronglyReportedAsConflicting != null) {
            throw new TeamsNotUniqueException(gameWronglyReportedAsConflicting);
        }
        TeamPair teams = game.teams();
        TeamName homeTeam = teams.homeTeam();
        TeamName awayTeam = teams.awayTeam();
        boolean homeTeamIsBusy = isBusy(homeTeam);
        boolean awayTeamIsBusy = isBusy(awayTeam);
        if (homeTeamIsBusy || awayTeamIsBusy) {
            throw new TeamsNotUniqueException(gameInProgress);
        }
        addedGame = game;
        long sequenceNumber = ASSIGNED_ID.value();
        Score score = game.score();
        int scoreRevision = game.scoreRevision();
        return new Game(ASSIGNED_ID, sequenceNumber, teams, score, scoreRevision);
    }

    private boolean isBusy(TeamName team) {
        if (gameInProgress == null) {
            return false;
        }
        TeamPair busyTeams = gameInProgress.getTeams();
        TeamName busyHomeTeam = busyTeams.homeTeam();
        TeamName busyAwayTeam = busyTeams.awayTeam();
        boolean playsAtHome = team.equals(busyHomeTeam);
        boolean playsAway = team.equals(busyAwayTeam);
        return playsAtHome || playsAway;
    }

    @Override
    public void updateScoreIfNextRevision(GameScore score) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGame(GameId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Game> findAllGames() {
        throw new UnsupportedOperationException();
    }
}
