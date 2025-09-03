package com.innovactions.incident.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incident")
public class IncidentController {
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
}
