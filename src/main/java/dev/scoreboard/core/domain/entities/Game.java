package dev.scoreboard.core.domain.entities;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public class Game {
    private final GameId id;
    private final long sequenceNumber;
    private final TeamPair teams;
    private Score score;
    private int scoreRevision;

    public Game(GameId id, long sequenceNumber, TeamPair teams, Score score, int scoreRevision) {
        ensureIdIsNotNull(id);
        ensureTeamsAreNotNull(teams);
        ensureScoreIsNotNull(score);
        ensureScoreRevisionIsNotNegative(scoreRevision);
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.teams = teams;
        this.score = score;
        this.scoreRevision = scoreRevision;
    }

    public GameId getId() {
        return id;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public TeamPair getTeams() {
        return teams;
    }

    public Score getScore() {
        return score;
    }

    public int getScoreRevision() {
        return scoreRevision;
    }

    public void updateScore(Score score, int scoreRevision) {
        this.score = score;
        this.scoreRevision = scoreRevision;
    }

    private static void ensureIdIsNotNull(GameId id) {
        if (id == null) {
            throw new NullPointerException("Game ID must not be null");
        }
    }

    private static void ensureTeamsAreNotNull(TeamPair teams) {
        if (teams == null) {
            throw new NullPointerException("Teams must not be null");
        }
    }

    private static void ensureScoreIsNotNull(Score score) {
        if (score == null) {
            throw new NullPointerException("Score must not be null");
        }
    }

    private static void ensureScoreRevisionIsNotNegative(int scoreRevision) {
        if (scoreRevision < 0) {
            throw new IllegalArgumentException("Score revision must not be negative");
        }
    }
}
