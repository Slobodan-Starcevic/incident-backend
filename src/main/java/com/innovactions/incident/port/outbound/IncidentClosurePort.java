package com.innovactions.incident.port.outbound;

public interface IncidentClosurePort {
    void closeIncident(String devId, String channelId, String reason);
}
