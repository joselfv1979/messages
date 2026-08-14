package com.messageapp.controller;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.messageapp.config.SecurityConfig;
import com.messageapp.dto.MessageRequest;
import com.messageapp.exception.ResourceNotFoundException;
import com.messageapp.exception.handler.GlobalExceptionHandler;
import com.messageapp.model.Message;
import com.messageapp.model.User;
import com.messageapp.repository.UserRepository;
import com.messageapp.security.AuthenticatedUserService;
import com.messageapp.security.CustomUserDetailsService;
import com.messageapp.security.JwtAuthenticationFilter;
import com.messageapp.security.JwtService;
import com.messageapp.service.MessageService;

@WebMvcTest(MessageController.class)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    JwtService.class,
    CustomUserDetailsService.class,
    GlobalExceptionHandler.class
})
@TestPropertySource(properties = {
    "jwt.secret=ThisIsATestSecretKeyThatIsLongEnoughForHS256Signing123456789",
    "jwt.expiration=3600000"
})
class MessageControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private UserRepository userRepository;

    private User user;

    private String token;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id("user-123")
                .username("jose")
                .password("encoded-password")
                .build();

        token = jwtService.generateToken(user.getUsername());

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);
    }

    @Test
    void shouldReturn404WhenMessageDoesNotExist() throws Exception {

        String messageId = "message-123";

        when(messageService.getById(messageId, user.getId()))
                .thenThrow(
                        new com.messageapp.exception.ResourceNotFoundException(
                                "Message not found"));

        mockMvc.perform(
                get("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Message not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/messages/" + messageId));
    }

    @Test
    void shouldGetMessageById() throws Exception {

        String messageId = "message-123";

        Message message = Message.builder()
                .id(messageId)
                .userId(user.getId())
                .title("Test title")
                .body("Test body")
                .build();

        when(messageService.getById(messageId, user.getId()))
                .thenReturn(message);

        mockMvc.perform(
                get("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.body").value("Test body"));
    }

    @Test
    void shouldCreateMessage() throws Exception {

        Message message = Message.builder()
                .id("message-123")
                .userId(user.getId())
                .title("Test title")
                .body("Test body")
                .build();

        when(messageService.create(any(MessageRequest.class), eq(user.getId())))
                .thenReturn(message);

        mockMvc.perform(
                post("/api/messages")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Test title",
                                "body": "Test body"
                            }
                            """)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("message-123"))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.body").value("Test body"));
    }

    @Test
    void shouldReturn400WhenCreateMessageRequestIsInvalid() throws Exception {

        mockMvc.perform(
                post("/api/messages")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "",
                                "body": ""
                            }
                            """)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/messages"))
                .andExpect(jsonPath("$.validationErrors").exists());

        verify(messageService, never())
                .create(any(MessageRequest.class), eq(user.getId()));
    }

    @Test
    void shouldUpdateMessage() throws Exception {

        String messageId = "message-123";

        Message message = Message.builder()
                .id(messageId)
                .userId(user.getId())
                .title("Updated title")
                .body("Updated body")
                .build();

        when(messageService.update(
                eq(messageId),
                any(MessageRequest.class),
                eq(user.getId())))
                .thenReturn(message);

        mockMvc.perform(
                put("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Updated title",
                                "body": "Updated body"
                            }
                            """)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.body").value("Updated body"));

        verify(messageService).update(
                eq(messageId),
                any(MessageRequest.class),
                eq(user.getId()));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingMessage() throws Exception {

        String messageId = "message-123";

        when(messageService.update(
                eq(messageId),
                any(MessageRequest.class),
                eq(user.getId())))
                .thenThrow(
                        new ResourceNotFoundException("Message not found"));

        mockMvc.perform(
                put("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Updated title",
                                "body": "Updated body"
                            }
                            """)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Message not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/messages/" + messageId));

        verify(messageService).update(
                eq(messageId),
                any(MessageRequest.class),
                eq(user.getId()));
    }

    @Test
    void shouldDeleteMessage() throws Exception {

        String messageId = "message-123";

        doNothing()
                .when(messageService)
                .delete(messageId, user.getId());

        mockMvc.perform(
                delete("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message deleted"));

        verify(messageService)
                .delete(messageId, user.getId());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingMessage() throws Exception {

        String messageId = "message-123";

        doThrow(new ResourceNotFoundException("Message not found"))
                .when(messageService)
                .delete(messageId, user.getId());

        mockMvc.perform(
                delete("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Message not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/messages/" + messageId));

        verify(messageService)
                .delete(messageId, user.getId());
    }

    @Test
    void shouldGetAllMessages() throws Exception {

        Message message1 = Message.builder()
                .id("message-1")
                .userId(user.getId())
                .title("Title 1")
                .body("Body 1")
                .build();

        Message message2 = Message.builder()
                .id("message-2")
                .userId(user.getId())
                .title("Title 2")
                .body("Body 2")
                .build();

        when(messageService.getAllByUser(user.getId()))
                .thenReturn(java.util.List.of(message1, message2));

        mockMvc.perform(
                get("/api/messages")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("message-1"))
                .andExpect(jsonPath("$[0].userId").value(user.getId()))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].body").value("Body 1"))
                .andExpect(jsonPath("$[1].id").value("message-2"))
                .andExpect(jsonPath("$[1].userId").value(user.getId()))
                .andExpect(jsonPath("$[1].title").value("Title 2"))
                .andExpect(jsonPath("$[1].body").value("Body 2"));

        verify(messageService)
                .getAllByUser(user.getId());
    }

    @Test
    void shouldReturn401WhenNoTokenIsProvided() throws Exception {

        mockMvc.perform(
                get("/api/messages")
        )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(messageService);
        verifyNoInteractions(authenticatedUserService);
    }

    @Test
    void shouldReturn401WhenTokenIsInvalid() throws Exception {

        mockMvc.perform(
                get("/api/messages")
                        .header("Authorization", "Bearer invalid-token")
        )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(messageService);
        verifyNoInteractions(authenticatedUserService);
    }

    @Test
    void shouldAllowAccessWithValidToken() throws Exception {

        when(messageService.getAllByUser(user.getId()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                get("/api/messages")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(messageService)
                .getAllByUser(user.getId());

        verify(authenticatedUserService)
                .getCurrentUser();
    }
}
