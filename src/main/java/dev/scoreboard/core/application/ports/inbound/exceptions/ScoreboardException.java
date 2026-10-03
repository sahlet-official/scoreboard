package dev.scoreboard.core.application.ports.inbound.exceptions;

public abstract class ScoreboardException extends RuntimeException {
    protected ScoreboardException(String message) {
        super(message);
    }
}
