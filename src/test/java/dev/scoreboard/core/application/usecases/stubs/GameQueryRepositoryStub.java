package dev.scoreboard.core.application.usecases.stubs;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

public class GameQueryRepositoryStub implements GameRepository {
    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public Optional<Game> findGame(GameId id) {
        if (game == null) {
            return Optional.empty();
        }

        GameId idOfGame = game.getId();
        boolean found = id.equals(idOfGame);
        if (!found) {
            return Optional.empty();
        }

        return Optional.of(game);
    }

    @Override
    public Optional<Game> findGame(TeamPair teams) {
        if (game == null) {
            return Optional.empty();
        }

        TeamPair teamsOfGame = game.getTeams();
        boolean found = teams.equals(teamsOfGame);
        if (!found) {
            return Optional.empty();
        }

        return Optional.of(game);
    }

    @Override
    public Game addGameWithUniqueTeams(NewGame game) {
        throw new UnsupportedOperationException();
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
    public List<Game> findAllGames() {
        throw new UnsupportedOperationException();
    }
}
