package dev.scoreboard.core.application.usecases;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

class GameRepositoryStub implements GameRepository {
    static final GameId ASSIGNED_ID = new GameId(7);

    private NewGame addedGame;

    NewGame getAddedGame() {
        return addedGame;
    }

    @Override
    public Game addGameWithUniqueTeams(NewGame game) {
        addedGame = game;
        long sequenceNumber = ASSIGNED_ID.value();
        TeamPair teams = game.teams();
        Score score = game.score();
        int scoreRevision = game.scoreRevision();
        return new Game(ASSIGNED_ID, sequenceNumber, teams, score, scoreRevision);
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
