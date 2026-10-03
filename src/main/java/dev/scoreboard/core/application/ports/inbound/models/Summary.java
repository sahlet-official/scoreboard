package dev.scoreboard.core.application.ports.inbound.models;

import java.util.List;

public record Summary(List<GameSummary> games) {
}
