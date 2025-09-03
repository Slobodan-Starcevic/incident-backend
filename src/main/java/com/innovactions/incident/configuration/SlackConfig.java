package com.innovactions.incident.configuration;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import com.slack.api.methods.SlackApiException;
import jakarta.servlet.Servlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${SLACK_SIGNING_SECRET}")     private String signingSecret;
    @Value("${SLACK_BOT_TOKEN_A}")        private String botTokenA; // listens/responds
    @Value("${SLACK_BOT_TOKEN_B}")        private String botTokenB; // broadcasts
    @Value("${BROADCAST_CHANNEL}")        private String broadcastChannel;

    @Bean
    public App slackApp() {
        var appConfig = AppConfig.builder()
                .signingSecret(signingSecret)
                .singleTeamBotToken(botTokenA)
                .build();

        var app = new App(appConfig);

        // Setup listener
        app.event(com.slack.api.model.event.AppMentionEvent.class, (payload, context) -> {
            var event = payload.getEvent();
            var userId = event.getUser();
            var text   = event.getText();

            // Get slack username
            var reporterName = userId;
            try {
                var info = context.client().usersInfo(r -> r.user(userId));
                if (info.isOk() && info.getUser() != null && info.getUser().getProfile() != null) {
                    reporterName = info.getUser().getProfile().getRealName();
                }
            } catch (Exception ignore) {}

            // Categorize & assign (dummy for now until Yir's implementation
            var severity = categorize(text);
            var assigned = "Bob";

            var summary = """
                    📢 New Incident Reported!
                    Reporter: %s
                    Severity: %s
                    Assigned Developer: %s
                    Details: %s
                    """.formatted(reporterName, severity, assigned, text);

            // Reply in Workspace A (thread/channel where mentioned)
            context.say("Incident noted! Assigned to *%s* with severity *%s*".formatted(assigned, severity));

            // Broadcast to Workspace B
            try {
                Slack.getInstance().methods(botTokenB).chatPostMessage(r -> r
                        .channel(broadcastChannel)
                        .text(summary));
            } catch (SlackApiException | java.io.IOException e) {
                System.err.println("Slack API returned an error: " + e.getMessage());
            }

            return context.ack();
        });

        return app;
    }

    // Register servlet
    @Bean
    public ServletRegistrationBean<Servlet> slackServlet(App app) {
        return new ServletRegistrationBean<>(new SlackAppServlet(app), "/slack/events");
    }

    // dummy categorization for now.
    static String categorize(String text) {
        var t = text == null ? "" : text.toLowerCase();
        if (t.contains("urgent")) return "URGENT";
        if (t.contains("fail") || t.contains("error")) return "MAJOR";
        return "MINOR";
    }
}
