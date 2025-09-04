package com.innovactions.incident.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "slack")
public class SlackProperties {
    private String signingSecret;
    private String botTokenA;
    private String botTokenB;
    private String broadcastChannel;
}