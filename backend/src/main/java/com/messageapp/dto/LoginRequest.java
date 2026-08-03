package com.messageapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @Schema(
        description = "Registered username",
        example = "john_doe"
    )
    @NotBlank(message = "Username is required")
    String username,

    @Schema(
        description = "User's password",
        example = "SecurePass123"
    )
    @NotBlank(message = "Password is required")
    String password

) {}