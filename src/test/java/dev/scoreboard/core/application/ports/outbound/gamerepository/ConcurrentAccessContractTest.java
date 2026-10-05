package dev.scoreboard.core.application.ports.outbound.gamerepository;

import static dev.scoreboard.core.application.ports.outbound.gamerepository.GameRepositoryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

import dev.scoreboard.core.application.ports.outbound.GameRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.RepeatedTest;

public interface ConcurrentAccessContractTest {
    int REPETITIONS = 50;
    int THREADS = 32;

    GameRepository gameRepository();

    @RepeatedTest(REPETITIONS)
    default void shouldAddOnlyOneOfGamesWithSameTeamAddedAtTheSameTime() {
        ThrowingCallable additionOfGame = () -> gameRepository().addGameWithUniqueTeams(NEW_GAME);
        ThrowingCallable additionOfGameWithSameTeam =
            () -> gameRepository().addGameWithUniqueTeams(NEW_GAME_WITH_SAME_HOME_TEAM);

        int successfulAdditions = countSuccessfulAttempts(additionOfGame, additionOfGameWithSameTeam);

        assertThat(successfulAdditions).isEqualTo(1);
    }

    private static int countSuccessfulAttempts(ThrowingCallable... attempts) {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch threadsAreReady = new CountDownLatch(THREADS);
        AtomicBoolean started = new AtomicBoolean(false);

        try {
            List<Future<Boolean>> outcomes = new ArrayList<>();
            for (int thread = 0; thread < THREADS; thread++) {
                ThrowingCallable attempt = attempts[thread % attempts.length];
                Callable<Boolean> task = () -> {
                    threadsAreReady.countDown();
                    waitForStart(started);
                    return isSuccessful(attempt);
                };
                Future<Boolean> outcome = executor.submit(task);
                outcomes.add(outcome);
            }

            threadsAreReady.await();
            started.set(true);

            int successfulAttempts = 0;
            for (Future<Boolean> outcome : outcomes) {
                boolean successful = outcome.get();
                if (successful) {
                    successfulAttempts++;
                }
            }
            return successfulAttempts;
        } catch (InterruptedException | ExecutionException exception) {
            throw new AssertionError("Attempt failed unexpectedly", exception);
        } finally {
            executor.shutdownNow();
        }
    }

    private static void waitForStart(AtomicBoolean started) {
        while (!started.get()) {
            Thread.onSpinWait();
        }
    }

    // A checked exception is a rejection defined by the port; anything else is a failure of the repository.
    private static boolean isSuccessful(ThrowingCallable attempt) {
        try {
            attempt.call();
            return true;
        } catch (RuntimeException | Error unexpectedFailure) {
            throw unexpectedFailure;
        } catch (Throwable rejection) {
            return false;
        }
    }
}
