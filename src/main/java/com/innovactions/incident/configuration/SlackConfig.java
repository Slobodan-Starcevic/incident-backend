package com.innovactions.incident.configuration;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.servlet.SlackAppServlet;
import com.slack.api.methods.SlackApiException;
import jakarta.servlet.Servlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${slack.signingSecret}")    private String signingSecret;
    @Value("${slack.botTokenA}")        private String botTokenA; // listens/responds
    @Value("${slack.botTokenB}")        private String botTokenB; // broadcasts
    @Value("${slack.broadcastChannel}") private String broadcastChannel;

    @Bean
    public App slackApp() {
        var appConfig = AppConfig.builder()
                .signingSecret(signingSecret)
                .singleTeamBotToken(botTokenA)
                .build();

        var app = new App(appConfig);

        // === Handlers ===
        app.event(com.slack.api.model.event.AppMentionEvent.class, (payload, ctx) -> {
            var event = payload.getEvent();
            var userId = event.getUser();
            var text   = event.getText();

            // Lookup reporter name
            var reporterName = userId;
            try {
                var info = ctx.client().usersInfo(r -> r.user(userId));
                if (info.isOk() && info.getUser() != null && info.getUser().getProfile() != null) {
                    reporterName = info.getUser().getProfile().getRealName();
                }
            } catch (Exception ignore) {}

            // Categorize & assign
            var severity = categorize(text);
            var assigned = AssigneePool.next(); // simple round-robin

            var summary = """
                    📢 New Incident Reported!
                    Reporter: %s
                    Severity: %s
                    Assigned Developer: %s
                    Details: %s
                    """.formatted(reporterName, severity, assigned, text);

            // Reply in Workspace A (thread/channel where mentioned)
            ctx.say("Incident noted! Assigned to *%s* with severity *%s*".formatted(assigned, severity));

            // Broadcast to Workspace B
            try {
                Slack.getInstance().methods(botTokenB).chatPostMessage(r -> r
                        .channel(broadcastChannel)
                        .text(summary));
            } catch (SlackApiException | java.io.IOException e) {
                // log in real code
            }

            return ctx.ack();
        });

        return app;
    }

    // Register Bolt's servlet at /slack/events (handles URL verification + event dispatch)
    @Bean
    public ServletRegistrationBean<Servlet> slackServlet(App app) {
        return new ServletRegistrationBean<>(new SlackAppServlet(app), "/slack/events");
    }

    // ---- helpers ----
    static String categorize(String text) {
        var t = text == null ? "" : text.toLowerCase();
        if (t.contains("urgent")) return "URGENT";
        if (t.contains("fail") || t.contains("error")) return "MAJOR";
        return "MINOR";
    }
}
