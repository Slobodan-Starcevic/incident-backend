package com.innovactions.incident.domain.command;

public record ReportIncidentCommand(
        String source,            // "slack"
        String reporterExternalId,// e.g. Slack user id
        String rawText            // original text
) {}
