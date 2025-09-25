package com.innovactions.incident.adapter.inbound.slack;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.slack.api.Slack;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.event.AppMentionEvent;

import java.io.IOException;
import java.time.Instant;

public class SlackCreateIncident {

    private final IncidentInboundPort incidentInboundPort;

    public SlackCreateIncident(IncidentInboundPort incidentInboundPort) {
        this.incidentInboundPort = incidentInboundPort;
    }

    public void handle(AppMentionEvent event, EventContext context) throws SlackApiException, IOException {
        String userId = event.getUser();
        String rawText = event.getText();

        // Slack message by default are prepended with a <@----> user id, we remove this for a clean message.
        String cleanText = rawText.replaceAll("<@[A-Z0-9]+(?:\\|[^>]+)?>", "").trim();

        String reporterName = userId;

        // Request the username from Slack
        try {
            // Explicitly pass a builder object for more fine control, needed for testing.
            UsersInfoResponse info = Slack
                    .getInstance()
                    .methods()
                    .usersInfo(
                            UsersInfoRequest
                                    .builder()
                                    .user(userId)
                                    .build()
                    );
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
