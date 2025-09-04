package com.innovactions.incident.application.command;

import java.time.Instant;

public record CreateIncidentCommand(
        String reporterId,
        String reporterName,
        String message,
        Instant reportedAt
) {}
