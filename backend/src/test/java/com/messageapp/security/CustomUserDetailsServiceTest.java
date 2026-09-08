package com.messageapp.security;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.messageapp.model.User;
import com.messageapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {

        // Arrange
        User user = User.builder()
                .id("user-id")
                .username("jose")
                .password("encoded-password")
                .build();

        when(userRepository.findByUsername("jose"))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails result
                = customUserDetailsService.loadUserByUsername("jose");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("jose");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("USER");

        verify(userRepository)
                .findByUsername("jose");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        // Arrange
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(()
                -> customUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("unknown");

        verify(userRepository)
                .findByUsername("unknown");
    }
}
