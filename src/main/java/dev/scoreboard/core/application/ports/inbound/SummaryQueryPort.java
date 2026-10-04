package dev.scoreboard.core.application.ports.inbound;

import dev.scoreboard.core.application.ports.inbound.models.Summary;

public interface SummaryQueryPort {
    Summary execute();
}
