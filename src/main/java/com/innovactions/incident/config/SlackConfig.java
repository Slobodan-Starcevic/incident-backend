package com.innovactions.incident.config;

import com.innovactions.incident.adapter.inbound.SlackInboundAdapter;
import com.innovactions.incident.adapter.outbound.SlackBroadcaster;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import jakarta.servlet.Servlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${slack.signingSecret}")
    private String signingSecret;

    @Value("${slack.botTokenA}")
    private String botTokenA;

    @Bean
    public App slackApp(SlackInboundAdapter slackInboundAdapter) {
        var appConfig = AppConfig.builder()
                .signingSecret(signingSecret)
                .singleTeamBotToken(botTokenA)
                .build();

        var app = new App(appConfig);

        app.event(com.slack.api.model.event.AppMentionEvent.class,
                (payload, context) -> {
                    slackInboundAdapter.handle(payload.getEvent(), context);
                    context.say("Incident noted! Thanks, we’ll look into it.");
                    return context.ack();
                });

        return app;
    }

    @Bean
    public ServletRegistrationBean<Servlet> slackServlet(App app) {
        return new ServletRegistrationBean<>(new SlackAppServlet(app), "/slack/events");
    }

    @Bean
    public SlackInboundAdapter slackIncomingAdapter(IncidentInboundPort incidentInboundPort) {
        return new SlackInboundAdapter(incidentInboundPort);
    }
    @Bean
    public IncidentBroadcasterPort slackBroadcaster(
            @Value("${slack.botTokenA}") String botTokenA,
            @Value("${slack.broadcastChannel}") String broadcastChannel
    ) {
        return new SlackBroadcaster(botTokenA, broadcastChannel);
    }
//    @Bean
//    public IncidentBroadcasterPort slackBroadcaster(
//            @Value("${slack.botTokenA}") String botTokenB,
//            @Value("${slack.broadcastChannel}") String broadcastChannel
//    ) {
//        return new SlackBroadcaster(botTokenB, broadcastChannel);
//    }

}
