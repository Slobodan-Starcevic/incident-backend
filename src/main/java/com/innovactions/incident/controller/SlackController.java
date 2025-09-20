package com.innovactions.incident.controller;

import com.innovactions.incident.domain.service.IncidentClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/slack/events")
public class SlackController {

    @Autowired
    private IncidentClosureService incidentClosureService;

    @PostMapping("/close_incident")
    public String closeIncident(
            @RequestParam String user_id,
            @RequestParam String channel_id,
            @RequestParam(required = false) String text) {
        
        String reason = text != null ? text : "No reason provided";
        
        // process incident closure asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                incidentClosureService.closeIncident(user_id, channel_id, reason);
            } catch (Exception e) {
                System.err.println("Error processing incident closure: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return "Processing incident closure...";
    }
}
