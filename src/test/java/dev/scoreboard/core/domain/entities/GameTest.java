package dev.scoreboard.core.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.Score;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameTest {
    private static final GameId ID = new GameId(7);
    private static final long SEQUENCE_NUMBER = 7;
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");
    private static final Score SCORE = new Score(0, 0);
    private static final int SCORE_REVISION = 0;

    @Test
    void shouldRejectMissingId() {
        GameId missingId = null;

        Throwable failure = catchThrowable(
            () -> new Game(missingId, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectMissingTeams() {
        TeamPair missingTeams = null;

        Throwable failure = catchThrowable(
            () -> new Game(ID, SEQUENCE_NUMBER, missingTeams, SCORE, SCORE_REVISION)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectMissingScore() {
        Score missingScore = null;

        Throwable failure = catchThrowable(
            () -> new Game(ID, SEQUENCE_NUMBER, TEAMS, missingScore, SCORE_REVISION)
        );

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, Integer.MIN_VALUE})
    void shouldRejectNegativeScoreRevision(int negativeRevision) {
        Throwable failure = catchThrowable(
            () -> new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, negativeRevision)
        );

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldKeepGivenValues() {
        Game game = new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION);

        boolean keepsId = game.getId().equals(ID);
        boolean keepsSequenceNumber = game.getSequenceNumber() == SEQUENCE_NUMBER;
        boolean keepsTeams = game.getTeams().equals(TEAMS);
        boolean keepsScore = game.getScore().equals(SCORE);
        boolean keepsScoreRevision = game.getScoreRevision() == SCORE_REVISION;
        
        boolean keepsGivenValues = keepsId
            && keepsSequenceNumber
            && keepsTeams
            && keepsScore
            && keepsScoreRevision;

        assertThat(keepsGivenValues).isTrue();
    }

    @Test
    void shouldChangeScoreWhenScoreIsUpdated() {
        Game game = new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION);
        Score newScore = new Score(1, 0);
        int newRevision = SCORE_REVISION + 1;

        game.updateScore(newScore, newRevision);
        Score currentScore = game.getScore();

        assertThat(currentScore).isEqualTo(newScore);
    }

    @Test
    void shouldChangeScoreRevisionWhenScoreIsUpdated() {
        Game game = new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION);
        Score newScore = new Score(1, 0);
        int newRevision = SCORE_REVISION + 1;

        game.updateScore(newScore, newRevision);
        int currentRevision = game.getScoreRevision();

        assertThat(currentRevision).isEqualTo(newRevision);
    }

    @Test
    void shouldRejectMissingScoreWhenScoreIsUpdated() {
        Game game = new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION);
        Score missingScore = null;
        int newRevision = SCORE_REVISION + 1;

        Throwable failure = catchThrowable(() -> game.updateScore(missingScore, newRevision));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, Integer.MIN_VALUE})
    void shouldRejectNegativeScoreRevisionWhenScoreIsUpdated(int negativeRevision) {
        Game game = new Game(ID, SEQUENCE_NUMBER, TEAMS, SCORE, SCORE_REVISION);
        Score newScore = new Score(1, 0);

        Throwable failure = catchThrowable(() -> game.updateScore(newScore, negativeRevision));

        assertThat(failure).isInstanceOf(IllegalArgumentException.class);
    }
}
