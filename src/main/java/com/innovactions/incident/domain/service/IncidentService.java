package com.innovactions.incident.domain.service;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.domain.model.Incident;
import com.innovactions.incident.domain.model.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IncidentService {

    public Incident createIncident(CreateIncidentCommand command, Severity severity) {
        String assignee = assign(command.message());

        Incident incident = new Incident(
                command.reporterId(),
                command.reporterName(),
                command.message(),
                severity,
                assignee
        );

        log.info("Created new incident: {}", incident.getId());
        return incident;
    }

    private String assign(String message) {
        return "Bob";
    }
}
