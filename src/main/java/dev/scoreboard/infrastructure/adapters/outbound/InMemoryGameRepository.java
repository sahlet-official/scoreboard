package dev.scoreboard.infrastructure.adapters.outbound;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.exceptions.GameMissingException;
import dev.scoreboard.core.application.ports.outbound.exceptions.ScoreRevisionConflictException;
import dev.scoreboard.core.application.ports.outbound.exceptions.TeamsNotUniqueException;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

public class InMemoryGameRepository implements GameRepository {
    @Override
    public Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateScoreIfNextRevision(GameScore score)
            throws GameMissingException, ScoreRevisionConflictException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeGame(GameId id) throws GameMissingException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Game> findAllGames() {
        return List.of();
    }
}
