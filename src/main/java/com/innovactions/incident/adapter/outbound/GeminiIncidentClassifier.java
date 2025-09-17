package com.innovactions.incident.adapter.outbound;

import com.innovactions.incident.domain.model.Severity;
import com.innovactions.incident.port.outbound.SeverityClassifierPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiIncidentClassifier implements SeverityClassifierPort {

    private final Client client;

    public GeminiIncidentClassifier() {
        // Reads GEMINI_API_KEY from environment variable
        this.client = new Client();
    }

    @Override
    public Severity classify(String message) {
        String prompt = """
                 You are an incident triage assistant.
                 Classify ONLY the severity for the user's report.
                \s
                 Options (return EXACTLY one token):
                 - URGENT: immediate attention, show-stopper, downtime, security breach, production outage.
                 - MAJOR: broken functionality but workarounds exist; handle within this week.
                 - MINOR: small bug/annoyance; can be deferred.
                \s
                 Return just one of: URGENT, MAJOR, MINOR.
                \s
                 Report:
                 ---
                 %s
                 ---
                """.formatted(message == null ? "" : message);

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );

        log.info("Gemini AI classified incident message '{}' as '{}'", message, response);

        try {
            return Severity.valueOf(Objects.requireNonNull(response.text()).trim().toUpperCase());
        } catch (NullPointerException e) {
            log.warn("Could not map response '{}', falling back to MINOR", response);
            return Severity.MINOR;
        }
    }
}
