package dev.scoreboard;

import dev.scoreboard.core.application.ports.outbound.GameRepository;

class ScoreboardWithGivenRepositoryTest implements ScoreboardTest {
    private final GameRepository gameRepository = InMemoryGameRepositoryFactory.createInMemoryGameRepository();
    private final Scoreboard scoreboard = ScoreboardFactory.createScoreboard(gameRepository);

    @Override
    public Scoreboard scoreboard() {
        return scoreboard;
    }
}
