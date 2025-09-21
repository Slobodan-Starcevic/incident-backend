package com.innovactions.incident.controller;

import com.innovactions.incident.domain.model.Incident;
import com.innovactions.incident.domain.model.Severity;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WhatsAppWebhookController {
    //for broadcasting incident
    private final IncidentBroadcasterPort slackBroadcaster;
    // Load VERIFY_TOKEN from environment variables or application.properties
    @Value("${whatsapp.verifyToken}")
    private String verifyToken;

    // GET endpoint for webhook verification
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(value = "hub.mode", required = false) String mode,
            @RequestParam(value = "hub.verify_token", required = false) String token,
            @RequestParam(value = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            System.out.println("WEBHOOK VERIFIED");
            return ResponseEntity.ok(challenge);  // Respond with challenge
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody Map<String, Object> body) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("WhatsApp payload received at {}", timestamp);
        log.debug("Raw WhatsApp payload: {}", body);

        try {
            Incident incident = mapToIncident(body);
            log.info("Mapped Incident object: {}", incident);

            // 🔥 Broadcast to Slack
            slackBroadcaster.broadcast(incident);

        } catch (Exception e) {
            log.error("Error while processing WhatsApp webhook", e);
        }

        return ResponseEntity.ok().build();
    }
    //FIXME: refactor this class and split
    private Incident mapToIncident(Map<String, Object> body) {
        //NOTE: Created Incident using "WhatsApp" as reporterId
        //FIXME: get whatsApp client Id that slack can reply to
        //TODO: implement outbound messaging could be reply or acknowledgement
        // Navigate payload: entry[0] → changes[0] → value
        Map entry = ((List<Map>) body.get("entry")).get(0);
        Map change = ((List<Map>) entry.get("changes")).get(0);
        Map value = (Map) change.get("value");

        // Contact info
        Map contact = ((List<Map>) value.get("contacts")).get(0);
        String senderName = (String) ((Map) contact.get("profile")).get("name");

        // Message
        Map message = ((List<Map>) value.get("messages")).get(0);
        String text = (String) ((Map) message.get("text")).get("body");

        // Build Incident
        return new Incident(
                "WhatsApp",     // reporterId (fixed for POC)
                senderName,     // reporterName
                text,
                Severity.MINOR, // TODO: plug in classifier
                "Unassigned"
        );
    }




}
