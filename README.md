# Live Football World Cup Score Board

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

## 2. Assumptions

1. **A team plays in only one game in progress.** An attempt to start a second game with that team is rejected.

2. **The order of teams in a pair matters.** Mexico - Canada and Canada - Mexico are different games. While the first one is in progress, the second one cannot be started: both teams are busy.

3. **A team name is any non-empty string with no leading or trailing whitespace.** Names are compared exactly and case-sensitively: "Mexico" and "mexico" are different teams. The client is responsible for consistent spelling (see trade-off 1).

4. **Few games are in progress at the same time.** Up to 10 in a normal situation; up to 100 in an extreme case that is not realistic in practice.

5. **The score is passed as an absolute pair of numbers.** The numbers are not negative and have no upper bound. The score can go down (a cancelled goal) and can change by more than one.

6. **The order of games in the summary depends only on the total score and on the order in which the games were started.** The order in which scores were updated does not affect it.

7. **The client needs to read a game.** The task does not list this operation. The client needs to find out whether a game between given teams is in progress, and what its score and score revision are. For this a game can be read by ID and by team pair.

## 3. Decisions

1. **A game has an ID.** The scoreboard assigns it when the game starts. Updating the score and finishing a game take the ID, not the team pair (see trade-off 2).

2. **A score update carries a revision number.** The client defines the order of updates (see trade-off 3).

3. **An update is accepted only if its revision is the current revision plus one.** Each game has its own revision, starting at 0.
   - Less than or equal to the current one: rejected, the update is stale.
   - More than the current one plus one: rejected.

4. **A score update is idempotent.** The score is absolute, so repeating the same update is safe: the current revision with the current score changes nothing and returns `UNCHANGED` instead of an error. The client can repeat a request when it does not know whether the first one got through.

5. **The summary returns data, not formatted text.** Teams and scores; how to display them is up to the client.

6. **The summary returns all games, without pagination.** There are few games (see assumption 4). An empty scoreboard gives an empty list.

7. **The interface is split into a reader and a writer.** This prepares for the time when instances for reading and for writing are created separately. For now the split is useful only when the scoreboard is passed to code that needs just one of the interfaces.

## 4. Trade-offs

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
