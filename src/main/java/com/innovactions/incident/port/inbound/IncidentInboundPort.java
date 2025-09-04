package com.innovactions.incident.port.inbound;

import com.innovactions.incident.application.command.CreateIncidentCommand;

public interface IncidentInboundPort {
    void handle(CreateIncidentCommand command);
}
