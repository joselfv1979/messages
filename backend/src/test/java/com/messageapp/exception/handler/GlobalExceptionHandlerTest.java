package com.messageapp.exception.handler;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.messageapp.exception.DuplicateUserException;
import com.messageapp.exception.ErrorResponse;
import com.messageapp.exception.ResourceNotFoundException;
import com.messageapp.exception.UnauthorizedException;
import com.messageapp.exception.ValidationErrorResponse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler
            = new GlobalExceptionHandler();

    private final MockHttpServletRequest request
            = new MockHttpServletRequest();

    @Test
    void shouldHandleUnauthorizedException() {

        request.setRequestURI("/api/messages");

        UnauthorizedException exception
                = new UnauthorizedException("Invalid credentials");

        ErrorResponse response
                = handler.handleUnauthorized(exception, request);

        assertThat(response).isNotNull();
        assertThat(response.timestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(response.status())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.error())
                .isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        assertThat(response.message())
                .isEqualTo("Invalid credentials");
        assertThat(response.path())
                .isEqualTo("/api/messages");
    }

    @Test
    void shouldHandleResourceNotFoundException() {

        request.setRequestURI("/api/messages/123");

        ResourceNotFoundException exception
                = new ResourceNotFoundException("Message not found");

        ErrorResponse response
                = handler.handleNotFound(exception, request);

        assertThat(response).isNotNull();
        assertThat(response.status())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.error())
                .isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(response.message())
                .isEqualTo("Message not found");
        assertThat(response.path())
                .isEqualTo("/api/messages/123");
    }

    @Test
    void shouldHandleDuplicateUserException() {

        request.setRequestURI("/api/auth/register");

        DuplicateUserException exception
                = new DuplicateUserException("Username already taken");

        ErrorResponse response
                = handler.handleDuplicateUser(exception, request);

        assertThat(response).isNotNull();
        assertThat(response.status())
                .isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.error())
                .isEqualTo(HttpStatus.CONFLICT.getReasonPhrase());
        assertThat(response.message())
                .isEqualTo("Username already taken");
        assertThat(response.path())
                .isEqualTo("/api/auth/register");
    }

    @Test
    void shouldHandleValidationErrors() {

        request.setRequestURI("/api/auth/register");

        Object target = new Object();

        BeanPropertyBindingResult bindingResult
                = new BeanPropertyBindingResult(target, "registerRequest");

        bindingResult.addError(
                new FieldError(
                        "registerRequest",
                        "username",
                        "Username is required"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "registerRequest",
                        "password",
                        "Password must contain at least 8 characters"
                )
        );

        MethodParameter methodParameter = mock(MethodParameter.class);

        MethodArgumentNotValidException exception
                = new MethodArgumentNotValidException(
                        methodParameter,
                        bindingResult
                );

        ValidationErrorResponse response
                = handler.handleValidationErrors(exception, request);

        assertThat(response).isNotNull();

        assertThat(response.status())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThat(response.error())
                .isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());

        assertThat(response.message())
                .isEqualTo("Validation failed");

        assertThat(response.path())
                .isEqualTo("/api/auth/register");

        assertThat(response.validationErrors())
                .containsEntry("username", "Username is required")
                .containsEntry(
                        "password",
                        "Password must contain at least 8 characters"
                );
    }
}
