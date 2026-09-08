package com.messageapp.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertThat(
                SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertThat(
                SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenTokenIsInvalid()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isTokenValid("invalid-token"))
                .thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(jwtService).isTokenValid("invalid-token");
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);

        assertThat(
                SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = User
                .withUsername("jose")
                .password("password")
                .authorities("USER")
                .build();

        when(jwtService.isTokenValid("valid-token"))
                .thenReturn(true);

        when(jwtService.extractUsername("valid-token"))
                .thenReturn("jose");

        when(userDetailsService.loadUserByUsername("jose"))
                .thenReturn(userDetails);

        filter.doFilter(request, response, filterChain);

        verify(jwtService).isTokenValid("valid-token");
        verify(jwtService).extractUsername("valid-token");
        verify(userDetailsService).loadUserByUsername("jose");
        verify(filterChain).doFilter(request, response);

        assertThat(
                SecurityContextHolder.getContext().getAuthentication())
                .isNotNull();

        assertThat(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .isEqualTo("jose");
    }

    @Test
    void shouldNotAuthenticateWhenUsernameIsNull()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isTokenValid("valid-token"))
                .thenReturn(true);

        when(jwtService.extractUsername("valid-token"))
                .thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(jwtService).isTokenValid("valid-token");
        verify(jwtService).extractUsername("valid-token");
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);

        assertThat(
                SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldNotAuthenticateWhenSecurityContextAlreadyContainsAuthentication()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails existingUser = User
                .withUsername("existing-user")
                .password("password")
                .authorities("USER")
                .build();

        var existingAuthentication
                = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        existingUser,
                        null,
                        existingUser.getAuthorities());

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        when(jwtService.isTokenValid("valid-token"))
                .thenReturn(true);

        when(jwtService.extractUsername("valid-token"))
                .thenReturn("jose");

        filter.doFilter(request, response, filterChain);

        verify(jwtService).isTokenValid("valid-token");
        verify(jwtService).extractUsername("valid-token");
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);

        assertThat(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .isEqualTo("existing-user");
    }

}
