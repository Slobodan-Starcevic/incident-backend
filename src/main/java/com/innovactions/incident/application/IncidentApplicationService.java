package com.innovactions.incident.application;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.domain.model.Incident;
import com.innovactions.incident.domain.model.Severity;
import com.innovactions.incident.domain.service.IncidentService;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import com.innovactions.incident.port.outbound.SeverityClassifierPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidentApplicationService implements IncidentInboundPort {

    private final IncidentService incidentService;
    private final IncidentBroadcasterPort broadcaster;
    private final SeverityClassifierPort severityClassifier;  // ⬅️ new

    @Override
    public void handle(CreateIncidentCommand command) {
        // Classify with OpenAI (or heuristic fallback)
        Severity severity = severityClassifier.classify(command.message());

        // Create the incident with the chosen severity
        Incident incident = incidentService.createIncident(command, severity);

        broadcaster.broadcast(incident);

        // (Ack-in-channel stays in SlackConfig via context.say for now)
    }
}
