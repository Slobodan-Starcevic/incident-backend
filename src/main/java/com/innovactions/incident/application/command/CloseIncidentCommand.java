package com.innovactions.incident.application.command;

public record CloseIncidentCommand (
    String developerUserId,
    String channelId,
    String reason
) {}
