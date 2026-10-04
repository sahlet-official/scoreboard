package dev.scoreboard.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import dev.scoreboard.core.application.ports.inbound.exceptions.GameNotFoundException;
import dev.scoreboard.core.application.usecases.stubs.GameByTeamsQueryRepositoryStub;
import dev.scoreboard.core.domain.valueobjects.TeamPair;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameByTeamsQueryUseCaseTest {
    private static final TeamPair TEAMS = TeamPair.of("Mexico", "Canada");

    private final GameByTeamsQueryRepositoryStub gameByTeamsQueryRepositoryStub =
        new GameByTeamsQueryRepositoryStub();
    private final GameByTeamsQueryUseCase gameByTeamsQueryUseCase =
        new GameByTeamsQueryUseCase(gameByTeamsQueryRepositoryStub);

    @Test
    void shouldRejectMissingTeams() {
        TeamPair missingTeams = null;

        Throwable failure = catchThrowable(() -> gameByTeamsQueryUseCase.execute(missingTeams));

        assertThat(failure).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectQueryWhenGameIsNotFound() {
        Throwable failure = catchThrowable(() -> gameByTeamsQueryUseCase.execute(TEAMS));

        assertThat(failure).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldReportTeamsOfGameThatIsNotFound() {
        GameNotFoundException exception = catchThrowableOfType(
            GameNotFoundException.class,
            () -> gameByTeamsQueryUseCase.execute(TEAMS)
        );
        Optional<TeamPair> reportedTeams = exception.getTeams();

        assertThat(reportedTeams).contains(TEAMS);
    }
}
