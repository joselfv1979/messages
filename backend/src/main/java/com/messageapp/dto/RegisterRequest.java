package com.messageapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Username is required")
    @Size(
        min = 3,
        max = 30,
        message = "Username must be between 3 and 30 characters"
    )
    @Pattern(
        regexp = "^[a-zA-Z0-9._]+$",
        message = "Username can only contain letters, numbers, '.' and '_'"
    )
    String username,

    @NotBlank(message = "Password is required")
    @Size(
        min = 8,
        max = 100,
        message = "Password must be between 8 and 100 characters"
    )
    String password

) {}