package com.messageapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messageapp.dto.AuthResponse;
import com.messageapp.dto.LoginRequest;
import com.messageapp.dto.RegisterRequest;
import com.messageapp.openapi.annotation.ConflictResponse;
import com.messageapp.openapi.annotation.UnauthorizedResponse;
import com.messageapp.openapi.annotation.ValidationErrorResponse;
import com.messageapp.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and authentication")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns a JWT token."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "User registered successfully"
    )
    @ValidationErrorResponse
    @ConflictResponse
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user and returns a JWT token."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Login successful"
    )
    @ValidationErrorResponse
    @UnauthorizedResponse
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
