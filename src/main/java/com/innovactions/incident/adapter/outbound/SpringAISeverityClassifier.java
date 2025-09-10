package com.innovactions.incident.adapter.outbound;

import com.google.genai.Client;
import com.innovactions.incident.domain.model.Severity;
import com.innovactions.incident.port.outbound.SeverityClassifierPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAISeverityClassifier implements SeverityClassifierPort {

    private final Client client;

    public SpringAISeverityClassifier() {
        // Reads GEMINI_API_KEY from environment variable
        this.client = new Client();
    }

    @Override
    public Severity classify(String message) {
        String prompt = """
            You are an incident triage assistant. 
            Classify ONLY the severity for the user's report.
            
            Options (return EXACTLY one token):
            - URGENT: immediate attention, show-stopper, downtime, security breach, production outage.
            - MAJOR: broken functionality but workarounds exist; handle within this week.
            - MINOR: small bug/annoyance; can be deferred.
            
            Return just one of: URGENT, MAJOR, MINOR.
            
            Report:
            ---
            %s
            ---
            """.formatted(message == null ? "" : message);


        String response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        ).text();

        log.info("Spring AI classified '{}' as raw='{}'", message, response);

        try {
            return Severity.valueOf(response.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Could not map response '{}', falling back to MINOR", response);
            return Severity.MINOR;
        }
    }
}