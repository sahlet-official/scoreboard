package dev.scoreboard.core.application.usecases.stubs;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import dev.scoreboard.core.application.ports.outbound.models.NewGame;
import dev.scoreboard.core.domain.entities.Game;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.List;
import java.util.Optional;

public class SummaryQueryRepositoryStub implements GameRepository {
    private List<Game> games = List.of();

    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public List<Game> findAllGames() {
        return games;
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
}
