package com.messageapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
    
    @Schema(
        description = "User identifier",
        example = "687e4d8bfe3dcb1c7d123456"
    )
    String id,

    @Schema(
        description = "Username",
        example = "john_doe"
    )
    String username,

    @Schema(
        description = "JWT access token"
    )
    String token
    
) {}
