package com.innovactions.incident.adapter.inbound;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.slack.api.Slack;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.event.AppMentionEvent;

import java.io.IOException;
import java.time.Instant;

public class SlackInboundAdapter {

    private final IncidentInboundPort incidentInboundPort;

    public SlackInboundAdapter(IncidentInboundPort incidentInboundPort) {
        this.incidentInboundPort = incidentInboundPort;
    }

    public void handle(AppMentionEvent event, EventContext context) throws SlackApiException, IOException {
        String userId = event.getUser();
        String rawText = event.getText();

        String cleanText = rawText.replaceAll("<@[A-Z0-9]+(?:\\|[^>]+)?>", "").trim();

        String reporterName = userId;

        try {
            UsersInfoResponse info = Slack.getInstance().methods().usersInfo(r -> r.user(userId));
            if (info.isOk() && info.getUser() != null && info.getUser().getProfile() != null) {
                reporterName = info.getUser().getProfile().getRealName();
            }
        } catch (IOException | com.slack.api.methods.SlackApiException e) {
            System.err.println("Slack API error resolving user info: " + e.getMessage());
        }

        CreateIncidentCommand command = new CreateIncidentCommand(
                userId,
                reporterName,
                cleanText,
                Instant.now()
        );

        incidentInboundPort.handle(command);
    }
}
