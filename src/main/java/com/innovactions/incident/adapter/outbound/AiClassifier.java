package com.innovactions.incident.adapter.outbound;

import com.innovactions.incident.domain.model.Severity;
import com.innovactions.incident.port.outbound.SeverityClassifierPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiClassifier implements SeverityClassifierPort {

    private final ChatModel chatModel;

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

        String response = ChatClient.create(chatModel)
                .prompt(prompt)
                .call()
                .content();

        log.info("AI classified incident message '{}' as '{}'", message, response);

        try {
            assert response != null;
            return Severity.valueOf(response.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Could not map response '{}', falling back to MINOR", response);
            return Severity.MINOR;
        }
    }
}