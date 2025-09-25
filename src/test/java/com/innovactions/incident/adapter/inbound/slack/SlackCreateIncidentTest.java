package com.innovactions.incident.adapter.inbound.slack;

import com.innovactions.incident.application.command.CreateIncidentCommand;
import com.innovactions.incident.port.inbound.IncidentInboundPort;
import com.slack.api.Slack;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.User;
import com.slack.api.model.event.AppMentionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SlackCreateIncident}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SlackCreateIncidentTest {

    @Mock
    private IncidentInboundPort incidentInboundPort;

    @Mock
    private AppMentionEvent appMentionEvent;

    @Mock
    private EventContext eventContext;

    @Mock
    private Slack slackInstance;

    @Mock
    private MethodsClient methodsClient;

    @Mock
    private UsersInfoResponse usersInfoResponse;

    @Mock
    private User user;

    @Mock
    private User.Profile userProfile;

    private SlackCreateIncident slackCreateIncident;

    @BeforeEach
    void setUp() {
        slackCreateIncident = new SlackCreateIncident(incidentInboundPort);
    }

    /**
     * Unit test for the {@link SlackCreateIncident#handle}.
     *
     * This test verifies whether the handle function correctly creates
     * an incident Command when it is triggered with valid values.
     *
     * @throws SlackApiException when requesting usersInfo
     * @throws IOException when requesting usersInfo
     */
    @Test
    void shouldCreateIncidentWithUserRealName() throws SlackApiException, IOException {
        // Arrange
        String userId = "U123456789";
        String rawText = "<@U987654321> There's a critical outage in production";
        String expectedCleanText = "There's a critical outage in production";
        String realName = "John Doe";

        when(appMentionEvent.getUser()).thenReturn(userId);
        when(appMentionEvent.getText()).thenReturn(rawText);

        try (MockedStatic<Slack> mockedSlack = mockStatic(Slack.class)) {
            mockedSlack.when(Slack::getInstance).thenReturn(slackInstance);
            when(slackInstance.methods()).thenReturn(methodsClient);
            when(methodsClient.usersInfo(any(UsersInfoRequest.class))).thenReturn(usersInfoResponse);
            when(usersInfoResponse.isOk()).thenReturn(true);
            when(usersInfoResponse.getUser()).thenReturn(user);
            when(user.getProfile()).thenReturn(userProfile);
            when(userProfile.getRealName()).thenReturn(realName);

            // Act
            slackCreateIncident.handle(appMentionEvent, eventContext);
        }

        // Assert
        ArgumentCaptor<CreateIncidentCommand> commandCaptor = ArgumentCaptor.forClass(CreateIncidentCommand.class);
        verify(incidentInboundPort).handle(commandCaptor.capture());

        CreateIncidentCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.reporterId()).isEqualTo(userId);
        assertThat(capturedCommand.reporterName()).isEqualTo(realName);
        assertThat(capturedCommand.message()).isEqualTo(expectedCleanText);
        assertThat(capturedCommand.reportedAt()).isNotNull();
    }

    @Test
    void shouldFallbackToUserIdWhenSlackApiCallFails() throws SlackApiException, IOException {
        // Given
        String userId = "U123456789";
        String rawText = "<@U987654321> Database connection lost";
        String expectedCleanText = "Database connection lost";

        when(appMentionEvent.getUser()).thenReturn(userId);
        when(appMentionEvent.getText()).thenReturn(rawText);

        // Mock Slack API to throw exception
        try (MockedStatic<Slack> mockedSlack = mockStatic(Slack.class)) {
            mockedSlack.when(Slack::getInstance).thenReturn(slackInstance);
            when(slackInstance.methods()).thenReturn(methodsClient);
            when(methodsClient.usersInfo((UsersInfoRequest) any())).thenThrow(new IOException("API Error"));

            // When
            slackCreateIncident.handle(appMentionEvent, eventContext);
        }

        // Then
        ArgumentCaptor<CreateIncidentCommand> commandCaptor = ArgumentCaptor.forClass(CreateIncidentCommand.class);
        verify(incidentInboundPort).handle(commandCaptor.capture());

        CreateIncidentCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.reporterId()).isEqualTo(userId);
        assertThat(capturedCommand.reporterName()).isEqualTo(userId); // Fallback to userId
        assertThat(capturedCommand.message()).isEqualTo(expectedCleanText);
    }

    @Test
    void shouldFallbackToUserIdWhenUserInfoIsNotOk() throws SlackApiException, IOException {
        // Given
        String userId = "U123456789";
        String rawText = "Server is down";

        when(appMentionEvent.getUser()).thenReturn(userId);
        when(appMentionEvent.getText()).thenReturn(rawText);

        try (MockedStatic<Slack> mockedSlack = mockStatic(Slack.class)) {
            mockedSlack.when(Slack::getInstance).thenReturn(slackInstance);
            when(slackInstance.methods()).thenReturn(methodsClient);
            when(methodsClient.usersInfo((UsersInfoRequest) any())).thenReturn(usersInfoResponse);
            when(usersInfoResponse.isOk()).thenReturn(false);

            // When
            slackCreateIncident.handle(appMentionEvent, eventContext);
        }

        // Then
        ArgumentCaptor<CreateIncidentCommand> commandCaptor = ArgumentCaptor.forClass(CreateIncidentCommand.class);
        verify(incidentInboundPort).handle(commandCaptor.capture());

        CreateIncidentCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.reporterName()).isEqualTo(userId);
    }

    @Test
    void shouldCleanMultipleMentionsFromText() throws SlackApiException, IOException {
        // Given
        String userId = "U123456789";
        String rawText = "<@U987654321> <@U111111111|someone> Critical issue here";
        String expectedCleanText = "Critical issue here";

        when(appMentionEvent.getUser()).thenReturn(userId);
        when(appMentionEvent.getText()).thenReturn(rawText);

        try (MockedStatic<Slack> mockedSlack = mockStatic(Slack.class)) {
            mockedSlack.when(Slack::getInstance).thenReturn(slackInstance);
            when(slackInstance.methods()).thenReturn(methodsClient);
            when(methodsClient.usersInfo((UsersInfoRequest) any())).thenReturn(usersInfoResponse);
            when(usersInfoResponse.isOk()).thenReturn(false);

            // When
            slackCreateIncident.handle(appMentionEvent, eventContext);
        }

        // Then
        ArgumentCaptor<CreateIncidentCommand> commandCaptor = ArgumentCaptor.forClass(CreateIncidentCommand.class);
        verify(incidentInboundPort).handle(commandCaptor.capture());

        CreateIncidentCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.message()).isEqualTo(expectedCleanText);
    }

    @Test
    void shouldHandleEmptyTextAfterCleaning() throws SlackApiException, IOException {
        // Given
        String userId = "U123456789";
        String rawText = "<@U987654321>";
        String expectedCleanText = "";

        when(appMentionEvent.getUser()).thenReturn(userId);
        when(appMentionEvent.getText()).thenReturn(rawText);

        try (MockedStatic<Slack> mockedSlack = mockStatic(Slack.class)) {
            mockedSlack.when(Slack::getInstance).thenReturn(slackInstance);
            when(slackInstance.methods()).thenReturn(methodsClient);
            when(methodsClient.usersInfo((UsersInfoRequest) any())).thenReturn(usersInfoResponse);
            when(usersInfoResponse.isOk()).thenReturn(false);

            // When
            slackCreateIncident.handle(appMentionEvent, eventContext);
        }

        // Then
        ArgumentCaptor<CreateIncidentCommand> commandCaptor = ArgumentCaptor.forClass(CreateIncidentCommand.class);
        verify(incidentInboundPort).handle(commandCaptor.capture());

        CreateIncidentCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.message()).isEqualTo(expectedCleanText);
    }

    @Test
    void shouldFallbackToUserIdWhenUserProfileIsNull() throws SlackApiException, IOException {
        // Given
        String userId = "U123456789";
        String rawText = "Test incident";

        when(appMentionEvent.getUser()).thenReturn(userId);
        when(appMentionEvent.getText()).thenReturn(rawText);

        try (MockedStatic<Slack> mockedSlack = mockStatic(Slack.class)) {
            mockedSlack.when(Slack::getInstance).thenReturn(slackInstance);
            when(slackInstance.methods()).thenReturn(methodsClient);
            when(methodsClient.usersInfo((UsersInfoRequest) any())).thenReturn(usersInfoResponse);
            when(usersInfoResponse.isOk()).thenReturn(true);
            when(usersInfoResponse.getUser()).thenReturn(user);
            when(user.getProfile()).thenReturn(null); // Profile is null

            // When
            slackCreateIncident.handle(appMentionEvent, eventContext);
        }

        // Then
        ArgumentCaptor<CreateIncidentCommand> commandCaptor = ArgumentCaptor.forClass(CreateIncidentCommand.class);
        verify(incidentInboundPort).handle(commandCaptor.capture());

        CreateIncidentCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.reporterName()).isEqualTo(userId);
    }
}