package dev.scoreboard;

import dev.scoreboard.core.application.ports.inbound.GameByTeamsQueryPort;
import dev.scoreboard.core.application.ports.inbound.GameFinishPort;
import dev.scoreboard.core.application.ports.inbound.GameQueryPort;
import dev.scoreboard.core.application.ports.inbound.GameStartPort;
import dev.scoreboard.core.application.ports.inbound.ScoreUpdatePort;
import dev.scoreboard.core.application.ports.inbound.SummaryQueryPort;

import dev.scoreboard.core.application.ports.inbound.models.GameDetails;
import dev.scoreboard.core.application.ports.inbound.models.Summary;
import dev.scoreboard.core.application.ports.inbound.models.UpdateScoreResult;
import dev.scoreboard.core.domain.valueobjects.GameId;
import dev.scoreboard.core.domain.valueobjects.GameScore;
import dev.scoreboard.core.domain.valueobjects.TeamPair;

class ScoreboardImpl implements Scoreboard {
    private final GameStartPort gameStartPort;
    private final ScoreUpdatePort scoreUpdatePort;
    private final GameFinishPort gameFinishPort;
    private final GameQueryPort gameQueryPort;
    private final GameByTeamsQueryPort gameByTeamsQueryPort;
    private final SummaryQueryPort summaryQueryPort;

    ScoreboardImpl(
            GameStartPort gameStartPort,
            ScoreUpdatePort scoreUpdatePort,
            GameFinishPort gameFinishPort,
            GameQueryPort gameQueryPort,
            GameByTeamsQueryPort gameByTeamsQueryPort,
            SummaryQueryPort summaryQueryPort) {
        this.gameStartPort = gameStartPort;
        this.scoreUpdatePort = scoreUpdatePort;
        this.gameFinishPort = gameFinishPort;
        this.gameQueryPort = gameQueryPort;
        this.gameByTeamsQueryPort = gameByTeamsQueryPort;
        this.summaryQueryPort = summaryQueryPort;
    }

    @Override
    public GameDetails startGame(TeamPair teams) {
        return gameStartPort.execute(teams);
    }

    @Override
    public UpdateScoreResult updateScore(GameScore score) {
        return scoreUpdatePort.execute(score);
    }

    @Override
    public void finishGame(GameId gameId) {
        gameFinishPort.execute(gameId);
    }

    @Override
    public GameDetails getGame(GameId gameId) {
        return gameQueryPort.execute(gameId);
    }

    @Override
    public GameDetails getGame(TeamPair teams) {
        return gameByTeamsQueryPort.execute(teams);
    }

    @Override
    public Summary getSummary() {
        return summaryQueryPort.execute();
    }
}
