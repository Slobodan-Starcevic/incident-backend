package com.innovactions.incident.application;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.domain.model.Incident;
import com.innovactions.incident.domain.service.IncidentService;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidentApplicationService implements IncidentInboundPort {

    private final IncidentService incidentService;
    private final IncidentBroadcasterPort broadcaster;

    @Override
    public void handle(CreateIncidentCommand command) {
        Incident incident = incidentService.createIncident(command);

        broadcaster.broadcast(incident);

        // we will do more in the future like persisting, etc.
    }
}

