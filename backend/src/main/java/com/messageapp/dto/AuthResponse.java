package com.messageapp.dto;

public record AuthResponse(
    String id,
    String username,
    String token
) {}
