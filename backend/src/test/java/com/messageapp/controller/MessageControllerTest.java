package com.messageapp.controller;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.messageapp.dto.MessageRequest;
import com.messageapp.dto.MessageResponse;
import com.messageapp.model.User;
import com.messageapp.security.AuthenticatedUserService;
import com.messageapp.service.MessageService;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private MessageController messageController;

    @Test
    void shouldGetAllMessagesForAuthenticatedUser() {

        // Arrange
        String userId = "user-123";

        User user = User.builder()
                .id(userId)
                .username("jose")
                .build();

        MessageResponse message1 = new MessageResponse(
                "message-1",
                "Title 1",
                "Body 1",
                userId,
                null,
                null);

        MessageResponse message2 = new MessageResponse(
                "message-2",
                "Title 2",
                "Body 2",
                userId,
                null,
                null);

        List<MessageResponse> messages = List.of(message1, message2);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(messageService.getAllByUser(userId))
                .thenReturn(messages);

        // Act
        var response = messageController.getAll();

        // Assert
        assertThat(response.getStatusCode().value())
                .isEqualTo(200);

        assertThat(response.getBody())
                .isEqualTo(messages);

        verify(authenticatedUserService)
                .getCurrentUser();

        verify(messageService)
                .getAllByUser(userId);
    }

    @Test
    void shouldGetMessageById() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        User user = User.builder()
                .id(userId)
                .username("jose")
                .build();

        MessageResponse message = new MessageResponse(
                messageId,
                "Test title",
                "Test body",
                userId,
                null,
                null);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(messageService.getById(messageId, userId))
                .thenReturn(message);

        // Act
        var response = messageController.getById(messageId);

        // Assert
        assertThat(response.getStatusCode().value())
                .isEqualTo(200);

        assertThat(response.getBody())
                .isSameAs(message);

        verify(authenticatedUserService)
                .getCurrentUser();

        verify(messageService)
                .getById(messageId, userId);
    }

    @Test
    void shouldCreateMessage() {

        // Arrange
        String userId = "user-123";

        User user = User.builder()
                .id(userId)
                .username("jose")
                .build();

        MessageRequest request = new MessageRequest(
                "Test title",
                "Test body"
        );

        MessageResponse message = new MessageResponse(
                "message-123",
                "Test title",
                "Test body",
                userId,
                null,
                null);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(messageService.create(request, userId))
                .thenReturn(message);

        // Act
        var response = messageController.create(request);

        // Assert
        assertThat(response.getStatusCode().value())
                .isEqualTo(201);

        assertThat(response.getBody())
                .isSameAs(message);

        verify(authenticatedUserService)
                .getCurrentUser();

        verify(messageService)
                .create(request, userId);
    }

    @Test
    void shouldUpdateMessage() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        User user = User.builder()
                .id(userId)
                .username("jose")
                .build();

        MessageRequest request = new MessageRequest(
                "Updated title",
                "Updated body"
        );

        MessageResponse updatedMessage = new MessageResponse(
                messageId,
                "Updated title",
                "Updated body",
                userId,
                null,
                null);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(messageService.update(messageId, request, userId))
                .thenReturn(updatedMessage);

        // Act
        var response = messageController.update(messageId, request);

        // Assert
        assertThat(response.getStatusCode().value())
                .isEqualTo(200);

        assertThat(response.getBody())
                .isSameAs(updatedMessage);

        verify(authenticatedUserService)
                .getCurrentUser();

        verify(messageService)
                .update(messageId, request, userId);
    }

    @Test
    void shouldDeleteMessage() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        User user = User.builder()
                .id(userId)
                .username("jose")
                .build();

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        // Act
        var response = messageController.delete(messageId);

        // Assert
        assertThat(response.getStatusCode().value())
                .isEqualTo(204);

        assertThat(response.getBody()).isNull();

        verify(authenticatedUserService)
                .getCurrentUser();

        verify(messageService)
                .delete(messageId, userId);
    }
}
