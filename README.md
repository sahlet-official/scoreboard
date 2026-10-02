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
