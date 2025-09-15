package com.innovactions.incident.port.outbound;

import com.innovactions.incident.domain.model.Severity;

public interface SeverityClassifierPort {
    Severity classify(String message);
}
