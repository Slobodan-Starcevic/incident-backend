package com.innovactions.incident.port.outbound;

import com.innovactions.incident.domain.model.Incident;

public interface IncidentBroadcasterPort {
    void broadcast(Incident incident);
}
