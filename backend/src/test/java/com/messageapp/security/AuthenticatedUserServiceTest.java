package com.messageapp.security;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.messageapp.exception.UnauthorizedException;
import com.messageapp.model.User;
import com.messageapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectWhenAuthenticationIsNull() {

        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThatThrownBy(()
                -> authenticatedUserService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectWhenAuthenticationIsNotAuthenticated() {

        // Arrange
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        // Act & Assert
        assertThatThrownBy(()
                -> authenticatedUserService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectAnonymousUser() {

        // Arrange
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn("anonymousUser");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        // Act & Assert
        assertThatThrownBy(()
                -> authenticatedUserService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldReturnAuthenticatedUser() {

        // Arrange
        User user = User.builder()
                .id("user-id")
                .username("jose")
                .password("password")
                .build();

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                        "jose",
                        null,
                        Collections.emptyList());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername("jose"))
                .thenReturn(Optional.of(user));

        // Act
        User result
                = authenticatedUserService.getCurrentUser();

        // Assert
        assertThat(result)
                .isSameAs(user);

        verify(userRepository)
                .findByUsername("jose");
    }

    @Test
    void shouldRejectWhenAuthenticatedUserDoesNotExist() {

        // Arrange
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                        "jose",
                        null,
                        Collections.emptyList());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername("jose"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(()
                -> authenticatedUserService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Authenticated user not found");

        verify(userRepository)
                .findByUsername("jose");
    }
}
