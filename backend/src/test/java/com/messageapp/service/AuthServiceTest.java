package com.messageapp.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.messageapp.dto.AuthResponse;
import com.messageapp.dto.LoginRequest;
import com.messageapp.dto.RegisterRequest;
import com.messageapp.exception.DuplicateUserException;
import com.messageapp.exception.UnauthorizedException;
import com.messageapp.model.User;
import com.messageapp.repository.UserRepository;
import com.messageapp.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldCreateAuthService() {

        assertThat(authService).isNotNull();
    }

    @Test
    void shouldRegisterNewUser() {

        // Arrange
        RegisterRequest request =
                new RegisterRequest("jose", "password123");

        when(userRepository.existsByUsername("jose"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id("123")
                .username("jose")
                .password("encodedPassword")
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken("jose"))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("123");
        assertThat(response.username()).isEqualTo("jose");
        assertThat(response.token()).isEqualTo("fake-jwt-token");

        verify(userRepository).existsByUsername("jose");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("jose");
    }

    @Test
    void shouldRejectRegistrationWhenUsernameAlreadyExists() {

        // Arrange
        RegisterRequest request =
                new RegisterRequest("jose", "password123");

        when(userRepository.existsByUsername("jose"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Username already taken");

        verify(userRepository).existsByUsername("jose");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        LoginRequest request =
                new LoginRequest("jose", "password123");

        User user = User.builder()
                .id("123")
                .username("jose")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("jose"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("jose"))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("123");
        assertThat(response.username()).isEqualTo("jose");
        assertThat(response.token()).isEqualTo("fake-jwt-token");

        verify(userRepository).findByUsername("jose");
        verify(passwordEncoder)
                .matches("password123", "encodedPassword");
        verify(jwtService).generateToken("jose");
    }

    @Test
    void shouldRejectLoginWhenUsernameDoesNotExist() {

        // Arrange
        LoginRequest request =
                new LoginRequest("unknown", "password123");

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository).findByUsername("unknown");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(anyString());
     }

     @Test
     void shouldRejectLoginWhenPasswordIsIncorrect() {

        // Arrange
        LoginRequest request =
                new LoginRequest("jose", "wrongPassword");

        User user = User.builder()
                .id("123")
                .username("jose")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("jose"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository).findByUsername("jose");

        verify(passwordEncoder)
                .matches("wrongPassword", "encodedPassword");

        verify(jwtService, never())
                .generateToken(anyString());
     }

}