package com.innovactions.incident.adapter.outbound;

import com.innovactions.incident.domain.model.Incident;
import com.innovactions.incident.domain.model.Severity;
import com.innovactions.incident.domain.service.ChannelNameGenerator;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsInviteResponse;
import com.slack.api.methods.response.conversations.ConversationsSetTopicResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@RequiredArgsConstructor
public class SlackBroadcaster implements IncidentBroadcasterPort {

    private static final Logger log = LoggerFactory.getLogger(SlackBroadcaster.class);

    private final String botToken;
    private final String developerUserId;
    private final ChannelNameGenerator channelNameGenerator;

    @Override
    public void broadcast(Incident incident) {
        try {
            // generate channel name based on severity and timestamp
            String channelName = channelNameGenerator.generateChannelName(incident.getSeverity());

            System.out.println(channelName);
            
            // create new channel in workspace B
            ConversationsCreateResponse createResponse = Slack.getInstance().methods(botToken)
                    .conversationsCreate(req -> req
                            .name(channelName)
                            .isPrivate(false)
                    );
            
            if (!createResponse.isOk()) {
                log.error("Failed to create channel for incident [{}]: {}", incident.getId(), createResponse.getError());
                return;
            }
            
            String channelId = createResponse.getChannel().getId();
            log.info("Created channel {} for incident: {}", channelName, incident.getId());
            
            // set channel topic with reporter information
            ConversationsSetTopicResponse topicResponse = Slack.getInstance().methods(botToken)
                    .conversationsSetTopic(req -> req
                            .channel(channelId)
                            .topic("reporterid:" + incident.getReporterId() + "_slack")
                    );
            
            if (!topicResponse.isOk()) {
                log.warn("Failed to set topic for channel {}: {}", channelName, topicResponse.getError());
            }
            
            // invite developer to the channel
            ConversationsInviteResponse inviteResponse = Slack.getInstance().methods(botToken)
                    .conversationsInvite(req -> req
                            .channel(channelId)
                            .users(java.util.List.of(developerUserId))
                    );
            
            if (!inviteResponse.isOk()) {
                log.warn("Failed to invite developer to channel {}: {}", channelName, inviteResponse.getError());
            } else {
                log.info("Invited developer {} to channel {}", developerUserId, channelName);
            }
            
            // post incident summary to the new channel
            Slack.getInstance().methods(botToken).chatPostMessage(req -> req
                    .channel(channelId)
                    .text(incident.summary())
            );
            
            log.info("Incident broadcasted to new channel {}: {}", channelName, incident.getId());
            
        } catch (IOException | SlackApiException e) {
            log.error("Failed to broadcast incident [{}] to Slack: {}", incident.getId(), e.getMessage(), e);
        }
    }
}
