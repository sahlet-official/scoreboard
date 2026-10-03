package dev.scoreboard;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameAlreadyInProgressException;
import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.ports.inbound.exceptions.ScoreRevisionGapException;
import dev.scoreboard.core.application.ports.inbound.exceptions.StaleScoreRevisionException;
import dev.scoreboard.core.application.ports.inbound.exceptions.TeamAlreadyPlayingException;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.inbound.models.UpdateScoreResult;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

public interface ScoreboardWriter {
    /**
     * Starts a new game with the score 0 - 0 and the score revision 0.
     *
     * @throws GameAlreadyInProgressException if a game is already in progress
     * @throws TeamAlreadyPlayingException if one of the teams is playing in another game
     */
    GameDetails startGame(TeamPair teams);

    /**
     * Sets the score of a game. The score revision must be the current revision plus one.
     * Sending the current revision with the current score changes nothing.
     *
     * @throws GameNotFoundException if there is no such game in progress
     * @throws StaleScoreRevisionException if the revision is less than or equal to the current one
     * @throws ScoreRevisionGapException if the revision is more than one ahead of the current one
     */
    UpdateScoreResult updateScore(GameScore score);

    /**
     * Finishes a game and removes it from the scoreboard.
     *
     * @throws GameNotFoundException if there is no such game in progress
     */
    void finishGame(GameId gameId);

    /**
     * Returns a game in progress.
     *
     * @throws GameNotFoundException if there is no such game in progress
     */
    GameDetails getGame(GameId gameId);

    /**
     * Returns the game in progress between the given teams.
     *
     * @throws GameNotFoundException if there is no such game in progress
     */
    GameDetails getGame(TeamPair teams);
}
