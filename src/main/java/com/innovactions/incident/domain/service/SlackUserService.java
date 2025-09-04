package com.innovactions.incident.domain.service;

import com.slack.api.bolt.context.builtin.EventContext;
import org.springframework.stereotype.Service;

@Service
public class SlackUserService {

    public SlackUser getUserInfo(String userId, EventContext context) {
        try {
            var info = context.client().usersInfo(r -> r.user(userId));
            if (info.isOk() && info.getUser() != null && info.getUser().getProfile() != null) {
                return new SlackUser(
                        userId,
                        info.getUser().getProfile().getRealName(),
                        info.getUser().getProfile().getDisplayName()
                );
            }
        } catch (Exception e) {
            // TODO: Add proper logging
            System.err.println("Error while getting user info: " + e.getMessage());
        }
        return new SlackUser(userId, userId, userId); // fallback
    }
}
