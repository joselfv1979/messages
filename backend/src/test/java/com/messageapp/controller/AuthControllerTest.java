package com.messageapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.messageapp.dto.AuthResponse;
import com.messageapp.dto.LoginRequest;
import com.messageapp.dto.RegisterRequest;
import com.messageapp.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest(
                "jose",
                "password"
        );

        loginRequest = new LoginRequest(
                "jose",
                "password"
        );

        authResponse = new AuthResponse(
                "user-id",
                "jose",
                "test-jwt-token"
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange
        when(authService.register(registerRequest))
                .thenReturn(authResponse);

        // Act
        ResponseEntity<?> response
                = authController.register(registerRequest);

        // Assert
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isEqualTo(authResponse);

        verify(authService)
                .register(registerRequest);
    }

    @Test
    void shouldLoginUserSuccessfully() {

        // Arrange
        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        // Act
        ResponseEntity<?> response
                = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isEqualTo(authResponse);

        verify(authService)
                .login(loginRequest);
    }
}
