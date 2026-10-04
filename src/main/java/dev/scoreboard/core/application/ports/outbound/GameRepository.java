package dev.scoreboard.core.application.ports.outbound;

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

/**
 * Stores the games in progress.
 * Every method is atomic.
 */
public interface GameRepository {
    /**
     * Adds a game and assigns its ID.
     * The game is added only if none of its teams is playing in a game in progress.
     *
     * @throws TeamsNotUniqueException if one of the teams is playing in a game in progress
     */
    Game addGameWithUniqueTeams(NewGame game) throws TeamsNotUniqueException;

    /**
     * Sets the score and the score revision of a game.
     * The score is set only if the stored revision is the given revision minus one.
     *
     * @throws GameMissingException if there is no such game
     * @throws ScoreRevisionConflictException if the stored revision is different
     */
    void updateScoreIfNextRevision(GameScore gameScore)
            throws GameMissingException, ScoreRevisionConflictException;

    /**
     * Removes a game.
     *
     * @throws GameMissingException if there is no such game
     */
    void removeGame(GameId id) throws GameMissingException;

    Optional<Game> findGame(GameId id);

    Optional<Game> findGame(TeamPair teams);

    List<Game> findAllGames();
}
