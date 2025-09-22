package com.innovactions.incident.adapter.inbound.slack;

import com.innovactions.incident.application.command.CloseIncidentCommand;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/slack/events")
public class SlackController {

    private final IncidentInboundPort incidentInboundPort;

    public SlackController(IncidentInboundPort incidentInboundPort) {
        this.incidentInboundPort = incidentInboundPort;
    }

    @PostMapping("/close_incident")
    public String closeIncident(
            @RequestParam String user_id,
            @RequestParam String channel_id,
            @RequestParam(required = true) String text) {
        
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason for closing the incident must be provided.");
        }
        String reason = text;
        
        // process incident closure asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                CloseIncidentCommand command = new CloseIncidentCommand(user_id, channel_id, reason);


                incidentInboundPort.closeIncident(command);
            } catch (Exception e) {
                System.err.println("Error processing incident closure: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return "Processing incident closure...";
    }
}
