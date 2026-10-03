package dev.scoreboard.core.domain.valueobjects;

public record GameScore(GameId gameId, Score score, int scoreRevision) {
}
