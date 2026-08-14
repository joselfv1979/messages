package com.messageapp.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET =
            "this-is-a-test-secret-key-that-is-long-enough-for-hs256";

    private static final long EXPIRATION = 3600000;

    private final JwtService jwtService =
            new JwtService(SECRET, EXPIRATION);

    @Test
    void shouldGenerateToken() {

        // Act
        String token = jwtService.generateToken("jose");

        // Assert
        assertThat(token).isNotBlank();
    }

    @Test
    void shouldExtractUsernameFromToken() {

        // Arrange
        String token = jwtService.generateToken("jose");

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertThat(username).isEqualTo("jose");
    }

    @Test
    void shouldValidateGeneratedToken() {

        // Arrange
        String token = jwtService.generateToken("jose");

        // Act
        boolean valid = jwtService.isTokenValid(token);

        // Assert
        assertThat(valid).isTrue();
    }

    @Test
    void shouldRejectManipulatedToken() {

        // Arrange
        String token = jwtService.generateToken("jose");
        String manipulatedToken = token.substring(0, token.length() - 1) + "x";

        // Act
        boolean valid = jwtService.isTokenValid(manipulatedToken);

        // Assert
        assertThat(valid).isFalse();
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {

        // Arrange
        JwtService shortLivedJwtService =
                new JwtService(SECRET, 1);

        String token = shortLivedJwtService.generateToken("jose");

        Thread.sleep(10);

        // Act
        boolean valid = shortLivedJwtService.isTokenValid(token);

        // Assert
        assertThat(valid).isFalse();
    }
}
