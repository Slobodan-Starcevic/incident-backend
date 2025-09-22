package com.innovactions.incident.config;

import com.innovactions.incident.adapter.inbound.slack.SlackCloseIncident;
import com.innovactions.incident.adapter.inbound.slack.SlackCreateIncident;
import com.innovactions.incident.adapter.outbound.SlackBroadcaster;
import com.innovactions.incident.adapter.outbound.SlackIncidentClosureBroadcaster;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.innovactions.incident.port.outbound.IncidentBroadcasterPort;
import com.innovactions.incident.port.outbound.IncidentClosurePort;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import jakarta.servlet.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;

@Configuration
public class SlackConfig {

    private static final Logger log = LoggerFactory.getLogger(SlackConfig.class);

    @Value("${slack.signingSecret}")
    private String signingSecret;

    @Value("${slack.botTokenA}")
    private String botTokenA;

    @Bean
    public App slackApp(SlackCreateIncident slackInboundAdapter, SlackCloseIncident slackCloseIncident) {
        var appConfig = AppConfig.builder()
                .signingSecret(signingSecret)
                .singleTeamBotToken(botTokenA)
                .build();

        var app = new App(appConfig);

        app.event(com.slack.api.model.event.AppMentionEvent.class, (payload, context) -> {
            // acknowledge the event so slack doesnt retry
            var ack = context.ack();

            // process async
            CompletableFuture.runAsync(() -> {
                try {
                    slackInboundAdapter.handle(payload.getEvent(), context);
                    context.say("Incident noted! Thanks, we’ll look into it.");
                } catch (Exception e) {
                    // pass
                }
            });

            return ack;
        });

        // add slash command for closing incidents
        app.command("/close_incident", (req, ctx) -> {
            // process async
            CompletableFuture.runAsync(() -> {
                try {
                    slackCloseIncident.handle(req, ctx);
                    ctx.say("Incident noted! Thanks, we’ll look into it.");
                } catch (Exception e) {
                    // pass
                }
            });

            return ctx.ack("Processing incident closure...");
        });

        return app;
    }

    @Bean
    public ServletRegistrationBean<Servlet> slackServlet(App app) {
        return new ServletRegistrationBean<>(new SlackAppServlet(app), "/slack/events");
    }


    @Bean
    public SlackCreateIncident slackIncomingAdapter(IncidentInboundPort incidentInboundPort) {
        return new SlackCreateIncident(incidentInboundPort);
    }

    @Bean
    public SlackCloseIncident slackCloseIncident(IncidentInboundPort incidentInboundPort) {
        return new SlackCloseIncident(incidentInboundPort);
    }

    @Bean
    public IncidentBroadcasterPort slackBroadcaster(
            @Value("${slack.botTokenB}") String botTokenB,
            @Value("${slack.developerUserId}") String developerUserId,
            com.innovactions.incident.domain.service.ChannelNameGenerator channelNameGenerator
    ) {
        return new SlackBroadcaster(botTokenB, developerUserId, channelNameGenerator);
    }

    @Bean
    public IncidentClosurePort incidentClosureBroadcaster(
            @Value("${slack.botTokenB}") String botTokenB,
            @Value("${slack.botTokenA}") String botTokenA
    ) {
        return new SlackIncidentClosureBroadcaster(botTokenB, botTokenA);
    }

}
