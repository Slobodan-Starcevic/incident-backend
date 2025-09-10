package com.innovactions.incident.domain.service;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.domain.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IncidentService {

    public Incident createIncident(CreateIncidentCommand command) {
        Severity severity = classify(command.message());

        String assignee = assign(command.message());

        Incident incident = new Incident(
                command.reporterId(),
                command.reporterName(),
                command.message(),
                severity
//                assignee
        );

        log.info("Created new incident: {}", incident.getId());
        return incident;
    }

    private Severity classify(String message) {
        if (message == null) return Severity.MINOR;
        String t = message.toLowerCase();
        if (t.contains("urgent")) return Severity.URGENT;
        if (t.contains("fail") || t.contains("error")) return Severity.MAJOR;
        return Severity.MINOR;
    }

    private String assign(String message) {
        return "Bob";
    }
    public List<Dispatch> assignIncidents(List<Incident> incidents, List<Developer> developers) {

        if (incidents == null || incidents.isEmpty() || developers == null || developers.isEmpty()) {
            return new ArrayList<>();
        }

        List<Developer> availableDevelopers = developers.stream()
                .filter(developer -> "available".equalsIgnoreCase(developer.getAvailabilityStatus()))
                .collect(Collectors.toList());

        List<Dispatch> dispatches = new ArrayList<>();
        final int[] assigneeIndex = {0}; // mutable counter for round-robin

        incidents.stream()
                .filter(incident -> "OPEN".equalsIgnoreCase(incident.getStatus().toString()))
                .sorted((a, b) -> b.getSeverity().compareTo(a.getSeverity()))
                .forEach(incident -> {
                    Developer developer = availableDevelopers.get(assigneeIndex[0]);

                    if (developer != null && "available".equalsIgnoreCase(developer.getAvailabilityStatus())) {
                        Dispatch dispatch = new Dispatch(developer, incident);
                        dispatches.add(dispatch);

                        // Update Incident status
                        incident.setStatus(Status.IN_PROGRESS);
                        // assignee.setAvailabilityStatus("unavailable"); // optional
                    }

                    // round-robin update
                    assigneeIndex[0] = (assigneeIndex[0] + 1) % availableDevelopers.size();
                });

        return dispatches;
    }
}
