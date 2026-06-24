package com.messageapp.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(

    @NotBlank
    String title,

    @NotBlank
    String body

) {}
