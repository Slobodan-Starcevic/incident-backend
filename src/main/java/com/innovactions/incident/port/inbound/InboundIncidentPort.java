package com.innovactions.incident.port.inbound;

public interface InboundIncidentPort {
    void handle(CreateIncidentCommand command);
}
