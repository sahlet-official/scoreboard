package dev.scoreboard;

class InMemoryScoreboardTest implements ScoreboardTest {
    private final Scoreboard scoreboard = ScoreboardFactory.createInMemoryScoreboard();

    @Override
    public Scoreboard scoreboard() {
        return scoreboard;
    }
}
