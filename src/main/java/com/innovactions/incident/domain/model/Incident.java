// domain/Incident.java
package com.innovactions.incident.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class Incident {
    private final String id;
    private final String reporterExternalId;
    private final String reporterDisplayName;
    private final String source; // "slack", "email", etc.
    private final String text;
    private final Severity severity;
    private final String assignee;
    private final Instant createdAt;
}
