package com.innovactions.incident.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Incident {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id = UUID.randomUUID();

    @NonNull
    private final String reporterId;

    @NonNull
    private final String reporterName;

    @NonNull
    private String details;

    @NonNull
    private Severity severity;

    @NonNull
    private String assignee;

    private final Instant reportedAt = Instant.now();

    private Status status =  Status.OPEN;

    public boolean escalate() {
        return severity.next()
                .map(next -> {
                    this.severity = next;
                    return true;
                })
                .orElse(false);
    }

    public void resolve() {
        this.status = Status.RESOLVED;
    }

    public void reassign(String newAssignee) {
        this.assignee = newAssignee;
    }

    public void updateDetails(String newDetails) {
        this.details = newDetails;
    }

    public String summary() {
        return "📢 Incident [" + severity + "] — Assigned to " + assignee +
                " | Reporter: " + reporterName +
                " | Status: " + status +
                " | Details: " + details;
    }
}
