package dev.scoreboard.core.application.ports.outbound;

import dev.scoreboard.core.application.ports.outbound.gamerepository.GameAdditionContractTest;
import dev.scoreboard.core.application.ports.outbound.gamerepository.GameRemovalContractTest;
import dev.scoreboard.core.application.ports.outbound.gamerepository.ScoreUpdateContractTest;
import dev.scoreboard.core.application.ports.outbound.gamerepository.StoredGameIsolationContractTest;

public interface GameRepositoryContractTest
        extends GameAdditionContractTest,
                ScoreUpdateContractTest,
                GameRemovalContractTest,
                StoredGameIsolationContractTest {
}
