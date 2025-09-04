package com.innovactions.incident.adapter.outbound;

import com.innovactions.incident.domain.model.Incident;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@RequiredArgsConstructor
public class SlackBroadcaster implements IncidentBroadcasterPort {

    private static final Logger log = LoggerFactory.getLogger(SlackBroadcaster.class);

    private final String botToken;
    private final String channelId;

    @Override
    public void broadcast(Incident incident) {
        try {
            Slack.getInstance().methods(botToken).chatPostMessage(req -> req
                    .channel(channelId)
                    .text(incident.summary())
            );
            log.info("Incident broadcasted to Slack: {}", incident.getId());
        } catch (IOException | SlackApiException e) {
            log.error("Failed to broadcast incident [{}] to Slack: {}", incident.getId(), e.getMessage(), e);
        }
    }
}
