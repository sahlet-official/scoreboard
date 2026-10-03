package dev.scoreboard;

import dev.scoreboard.core.application.ports.inbound.models.Summary;

public interface ScoreboardReader {
    /**
     * Returns the games in progress ordered by total score, highest first.
     * Games with the same total score are ordered by the most recently started first.
     */
    Summary getSummary();
}
