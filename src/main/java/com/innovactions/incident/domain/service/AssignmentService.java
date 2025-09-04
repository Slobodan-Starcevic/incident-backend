package com.innovactions.incident.domain.service;

import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    public String assignDeveloper(SeverityEnum severity) {
        // TODO: Implement proper assignment logic
        return switch (severity) {
            case S1 -> "Senior Dev";
            case S2 -> "Mid Dev";
            case S3 -> "Junior Dev";
            case S4 -> "Backlog";
        };
    }
}