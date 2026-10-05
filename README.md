# Live Football World Cup Score Board

A library for a scoreboard of football games in progress. It can start a game, update the score, finish a game and return a summary of games ordered by total score.

**Why the solution is wider than the minimum.** The task asks to treat the library as "a small part of a longer-lived product", to make decisions that let it evolve, and pays special attention to the boundaries between components and to the assumptions. So:
- **Boundaries between components.** Business rules, storage and the public interface are separated. The storage is behind an interface: memory can be replaced with a database without touching the business logic (see decision 8).
- **Ambiguous places are defined explicitly.** For example, what to do with a late score update: a game has an ID, a score has a revision (see trade-offs 2 and 3).
- **The scoreboard is read often and from many threads.** The storage is thread-safe, reads are non-blocking (see assumptions 8 and 9, decision 10).

Where no groundwork is needed now, the solution stays minimal (see trade-offs 1, 4 and 5).

## 1. How to run

### 1.1 Tests in Docker

Run the command from the repository root.
- Linux, macOS: any terminal.
- Windows: PowerShell 7, Git Bash, WSL, cmd.

```
docker build -t scoreboard . && docker run --rm scoreboard
```

### 1.2 Tests on the host

Requires Java 21 or newer.

Linux / macOS:

```
./mvnw test
```

Windows:

```
.\mvnw.cmd test
```

## 2. How to use

```java
Scoreboard scoreboard = ScoreboardFactory.createInMemoryScoreboard();

TeamPair teams = TeamPair.of("Mexico", "Canada");
GameDetails game = scoreboard.startGame(teams);

int nextScoreRevision = game.scoreRevision() + 1;
GameScore gameScore = new GameScore(
   game.id(),
   new Score(0, 1),
   nextScoreRevision
);
scoreboard.updateScore(gameScore);

Summary summary = scoreboard.getSummary();

scoreboard.finishGame(game.id());
```

- **Score revision.** A game starts with the score 0 - 0 and the revision 0. Every update carries a revision that is one more than the current one.
- **Reading a game.** `getGame(id)` and `getGame(teams)` return the ID, the teams, the current score and the revision.
- **Summary.** The list of games in progress (teams and score) in the order required by the task.
- **Rejections.** Unchecked exceptions that extend `ScoreboardException`: `GameAlreadyInProgressException`, `TeamAlreadyPlayingException`, `GameNotFoundException`, `StaleScoreRevisionException`, `ScoreRevisionGapException`.
- **Your own storage.** `ScoreboardFactory.createScoreboard(gameRepository)` creates a scoreboard with any implementation of `GameRepository`.

## 3. Assumptions

1. **A team plays in only one game in progress.** An attempt to start a second game with that team is rejected.

2. **The order of teams in a pair matters.** Mexico - Canada and Canada - Mexico are different games. While the first one is in progress, the second one cannot be started: both teams are busy.

3. **A team name is any non-empty string with no leading or trailing whitespace.** Names are compared exactly and case-sensitively: "Mexico" and "mexico" are different teams. The client is responsible for consistent spelling (see trade-off 1).

4. **Few games are in progress at the same time.** Up to 10 in a normal situation; up to 100 in an extreme case that is not realistic in practice.

5. **The score is passed as an absolute pair of numbers.** The numbers are not negative and have no upper bound. The score can go down (a cancelled goal) and can change by more than one.

6. **The order of games in the summary depends only on the total score and on the order in which the games were started.** The order in which scores were updated does not affect it.

7. **The client needs to read a game.** The task does not list this operation. The client needs to find out whether a game between given teams is in progress, and what its score and score revision are. For this a game can be read by ID and by team pair.

8. **Writes are very rare, reads are very frequent.**

9. **For writes consistency matters more than availability, for reads availability matters more than freshness.**
   - Writes: on a failure it is better to reject writes for a while than to accept diverging data. A wrong score is not acceptable: it means lost trust in the data, money lost on bets and viewers reacting to false information, and there are a lot of viewers.
   - Reads: always answer, may lag behind, but never show a wrong score.

## 4. Decisions

1. **A game has an ID.** The scoreboard assigns it when the game starts. Updating the score and finishing a game take the ID, not the team pair (see trade-off 2).

2. **A score update carries a revision number.** The client defines the order of updates (see trade-off 3).

3. **An update is accepted only if its revision is the current revision plus one.** Each game has its own revision, starting at 0.
   - Less than or equal to the current one: rejected, the update is stale.
   - More than the current one plus one: rejected.

4. **A score update is idempotent.** The score is absolute, so repeating the same update is safe: the current revision with the current score changes nothing and returns `UNCHANGED` instead of an error. The client can repeat a request when it does not know whether the first one got through.

5. **The summary returns data, not formatted text.** Teams and scores; how to display them is up to the client.

6. **The summary returns all games, without pagination.** There are few games (see assumption 4). An empty scoreboard gives an empty list.

7. **The interface is split into a reader and a writer.** This prepares for the time when instances for reading and for writing are created separately. For now the split is useful only when the scoreboard is passed to code that needs just one of the interfaces.

8. **Every repository operation is atomic, the port has no transactions.** Atomicity is the contract of the port: the in-memory implementation provides it within a process, an adapter to a shared database provides it across servers. The business logic has no locks and knows nothing about threads and servers; the repository is the only point of coordination (see trade-off 5).

9. **The storage assigns the game ID on insert.** The same number defines the "most recently added" order (see trade-off 6).

10. **Reads in the in-memory repository are non-blocking.** A read does not wait for a write and does not hold up writes or other reads. All games are kept in an immutable snapshot. Writes go one at a time: a write builds a new snapshot and replaces the current one. A read takes the current snapshot without a lock and sees a consistent state. This was chosen because there are far more reads than writes (see assumption 8). The cost: every write copies the state; with few games (see assumption 4) this is not noticeable.

11. **The business logic trusts the repository contract.** The use cases do not double-check the answers of the repository and do not guard against a broken contract. The contract is written down as a set of tests that every implementation of `GameRepository` has to pass; the in-memory implementation passes it. The cost: an implementation with a bug that has not passed these tests gives the client a wrong answer instead of an explicit internal error.

## 5. Trade-offs

1. **Team names are not strictly validated.** The client is responsible for correct and consistent spelling.
   - Why: the client is not limited by extra rules, and the solution stays small.
   - Alternative: strict rules for team names. This is more reliable.
   - Why not: more code and tests, a more complex solution. It goes against the task's requirement for a simple solution.

2. **The client works with the game ID.** It has to keep the ID or get it by reading the game by team pair.
   - Why: a team pair is unique only while the game is in progress. As soon as the requirements grow (for example history, events or repeated matches), a game will have to be told apart by something else.
   - Alternative: a game is identified by its team pair. The client has nothing to keep.
   - Why not: a key built from team names would put extra constraints on the names and on the way they are compared. It could also conflict with future requirements.

3. **The client keeps track of the score revision.** When there are several sources of updates, they can find out the current revision by reading the game.
   - Why: the library cannot guarantee the order of updates. They reach it from several threads or processes in arbitrary order. Inside the library the order cannot be determined either: the system can suspend a thread between the method call and any action inside it, including reading the clock.
   - Alternative: no revision, last write wins. Simpler for the client.
   - Why not: a late update silently overwrites a newer score. This is true both for a future distributed system with delays and for the current version with an in-memory store in a single process.

4. **There is no separate API layer.** The facade only passes calls to the use cases; the client gets the models and exceptions of the application layer as they are.
   - Why: less code, which matches the task's requirement for a simple solution.
   - Alternative: duplicate in a separate API layer all the contracts the client depends on. The client would then not depend on internal packages.
   - Why not: a second set of models and exceptions makes the solution noticeably bigger.

5. **The rule "a team plays in only one game" is guaranteed by the repository.**
   - Why: the contract is simple, and any real storage can fulfil it without making the business layer more complex (a unique constraint).
   - Alternative: transactions in the port, the rule entirely in the business logic.
   - Why not: a lot of code and tests, a higher chance of mistakes, and it goes against the requirement for a simple solution. Besides, in a real database uniqueness would still be enforced by a constraint in the storage, so transactions do not take the rule out of the storage.

6. **Writes go through a single point, and it also assigns the IDs.**
   - Why: the order of games is exact, the moment a game is "added to the system" is the moment of insert. Unique teams and no score conflicts come without extra mechanisms. Real databases fit this well: a single leader with replicas, or a quorum with consensus (Raft).
   - Alternative: several write nodes, the ID is generated on the node (Snowflake, UUIDv7).
   - Why not: the order becomes approximate (it depends on clocks), duplicate games and score conflicts appear and have to be resolved. The gain, availability and scale of writes, is not needed with our low write load.

## 6. Future evolution

1. **Several instances with a shared storage.** This needs an adapter to a database: implement `GameRepository`, pass the contract test set (`GameRepositoryContractTest`) and create the scoreboard with `ScoreboardFactory.createScoreboard(...)`. Atomicity of operations is then provided by the database, the port and the business logic do not change.

2. **High availability of writes.** A storage with consensus (Raft) instead of a single leader with a replica; the port does not change.

3. **Transactions at the use case level.** Needed if the simple repository contract is not enough, for example when several games have to be changed atomically.

4. **Separate instances for reading and writing.** Reads go to replicas and a cache, writes go to the leader. The reader and writer interfaces are already split for this (see decision 7).

5. **Strict rules for team names.** For example case-insensitive comparison or a registry of teams (see trade-off 1).

6. **A separate API layer.** Its own models and exceptions; the client no longer depends on internal packages (see trade-off 4).

## 7. Tests

The solution was written with TDD: a test first, then the minimal code for it.

- **Value objects and the entity.** Creation rules and stored values.
- **Use cases.** Each one separately, with a hand-written stub of the repository.
- **Repository contract.** The test set `GameRepositoryContractTest` that every implementation of `GameRepository` has to pass: adding and finding games, updating the score, removing games, stored data staying independent of returned objects, concurrent access from many threads.
- **The scoreboard as a whole.** Also written as a contract test, `ScoreboardTest`.

The concurrency tests are probabilistic: 32 threads, 50 repetitions of each test.
