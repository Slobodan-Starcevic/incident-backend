package com.innovactions.incident.controller;

import com.innovactions.incident.domain.model.Dispatch;
import com.innovactions.incident.domain.service.IncidentService;
import com.innovactions.incident.dto.AssignIncidentsRequest;
import com.innovactions.incident.dto.AssignIncidentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incident")
public class IncidentController {
    private final IncidentService incidentService;

    @GetMapping
    public List<String> getAllIncidents() {
        return List.of("something");
    }

    @GetMapping("/{id}")
    public String getIncident(@PathVariable String id) {
        return "Incident with id: " + id;
    }

    @PostMapping
    public String createIncident(@RequestBody String description) {
        return "Incident Created";
    }

    @PostMapping("/assignment")
    public AssignIncidentsResponse assignIncidents(@RequestBody AssignIncidentsRequest request) {
        List<Dispatch> dispatches = incidentService.assignIncidents(request.getIncidents(), request.getTeamMembers());

        return AssignIncidentsResponse.builder()
                .assignedTasks(dispatches)
                .build();
    }
    @GetMapping("/test")
    public String test() {
        return "Hello, this is a test!";
    }
}
