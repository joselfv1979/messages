package com.messageapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.messageapp.dto.MessageRequest;
import com.messageapp.dto.MessageResponse;
import com.messageapp.exception.ResourceNotFoundException;
import com.messageapp.model.Message;
import com.messageapp.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void shouldGetAllMessagesByUser() {

        // Arrange
        String userId = "user-123";

        Message message1 = Message.builder()
                .id("message-1")
                .userId(userId)
                .title("Title 1")
                .body("Body 1")
                .build();

        Message message2 = Message.builder()
                .id("message-2")
                .userId(userId)
                .title("Title 2")
                .body("Body 2")
                .build();

        List<Message> messages = List.of(message1, message2);

        when(messageRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(messages);

        // Act
        List<MessageResponse> result = messageService.getAllByUser(userId);

        // Assert
        assertThat(result)
                .hasSize(2)
                .extracting(MessageResponse::id)
                .containsExactly("message-1", "message-2");

        verify(messageRepository)
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldGetMessageByIdWhenBelongsToUser() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        Message message = Message.builder()
                .id(messageId)
                .userId(userId)
                .title("Test title")
                .body("Test body")
                .build();

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        // Act
        MessageResponse result = messageService.getById(messageId, userId);

        // Assert
        assertThat(result.id()).isEqualTo(messageId);
        assertThat(result.title()).isEqualTo("Test title");
        assertThat(result.body()).isEqualTo("Test body");
        assertThat(result.userId()).isEqualTo(userId);

        verify(messageRepository).findById(messageId);
    }

    @Test
    void shouldThrowExceptionWhenMessageDoesNotExist() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(()
                -> messageService.getById(messageId, userId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Message not found");

        verify(messageRepository).findById(messageId);
    }

    @Test
    void shouldThrowExceptionWhenMessageBelongsToAnotherUser() {

        // Arrange
        String messageId = "message-123";
        String ownerUserId = "user-456";
        String requestingUserId = "user-123";

        Message message = Message.builder()
                .id(messageId)
                .userId(ownerUserId)
                .title("Private message")
                .body("Private body")
                .build();

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        // Act & Assert
        assertThatThrownBy(()
                -> messageService.getById(messageId, requestingUserId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Message not found");

        verify(messageRepository).findById(messageId);
    }

    @Test
    void shouldCreateMessage() {

        // Arrange
        String userId = "user-123";

        MessageRequest request = new MessageRequest(
                "Test title",
                "Test body"
        );

        Message savedMessage = Message.builder()
                .id("message-123")
                .title("Test title")
                .body("Test body")
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedMessage);

        // Act
        MessageResponse result = messageService.create(request, userId);

        // Assert
        assertThat(result.id()).isEqualTo("message-123");
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.title()).isEqualTo("Test title");
        assertThat(result.body()).isEqualTo("Test body");

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void shouldUpdateMessage() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        Message message = Message.builder()
                .id(messageId)
                .userId(userId)
                .title("Old title")
                .body("Old body")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        MessageRequest request = new MessageRequest(
                "New title",
                "New body"
        );

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        when(messageRepository.save(message))
                .thenReturn(message);

        // Act
        MessageResponse result = messageService.update(messageId, request, userId);

        // Assert
        assertThat(result.title()).isEqualTo("New title");
        assertThat(result.body()).isEqualTo("New body");
        assertThat(result.updatedAt()).isNotNull();

        verify(messageRepository).findById(messageId);
        verify(messageRepository).save(message);
    }

    @Test
    void shouldDeleteMessage() {

        // Arrange
        String messageId = "message-123";
        String userId = "user-123";

        Message message = Message.builder()
                .id(messageId)
                .userId(userId)
                .title("Test title")
                .body("Test body")
                .build();

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        // Act
        messageService.delete(messageId, userId);

        // Assert
        verify(messageRepository).findById(messageId);
        verify(messageRepository).delete(message);
    }
}
