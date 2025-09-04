package com.innovactions.incident.domain.service;

import java.time.LocalDateTime;

public class IncidentService {

    private final CategorizationService categorizationService;
    private final AssignmentService assignmentService;

    public IncidentService(CategorizationService categorizationService,
                           AssignmentService assignmentService) {
        this.categorizationService = categorizationService;
        this.assignmentService = assignmentService;
    }

    public IncidentEntity createIncident(String text, SlackUser reporter) {
        var cleanText = cleanMentions(text);
        var severity = categorizationService.categorize(cleanText);
        var assignedDev = assignmentService.assignDeveloper(severity);

        return new IncidentEntity(
                reporter.id(),
                reporter.realName(),
                cleanText,
                severity,
                assignedDev,
                LocalDateTime.now()
        );
    }

    private String cleanMentions(String text) {
        return text.replaceAll("<@[A-Z0-9]+(?:\\|[^>]+)?>", "").trim();
    }
}
