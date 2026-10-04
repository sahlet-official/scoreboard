package dev.scoreboard.core.application.usecases.stubs;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

public class ScoreUpdateRepositoryStub implements GameRepository {
    private Game gameInProgress;
    private GameScore updatedScore;

    public void setGameInProgress(Game gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public GameScore getUpdatedScore() {
        return updatedScore;
    }

    @Override
    public void updateScoreIfNextRevision(GameScore score)
            throws GameMissingException, ScoreRevisionConflictException {
        if (gameInProgress == null) {
            GameId gameId = score.gameId();
            throw new GameMissingException(gameId);
        }

        int storedRevision = gameInProgress.getScoreRevision();
        int givenRevision = score.scoreRevision();
        boolean revisionIsNext = givenRevision - 1 == storedRevision;
        if (!revisionIsNext) {
            throw new ScoreRevisionConflictException(gameInProgress);
        }

        updatedScore = score;
    }

    @Override
    public Game addGameWithUniqueTeams(NewGame game) {
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
