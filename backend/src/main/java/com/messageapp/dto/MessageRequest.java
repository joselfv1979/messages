package com.messageapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(

    @NotBlank(message = "Title is required")
    @Size(
        min = 1,
        max = 100,
        message = "Title must be between 1 and 100 characters"
    )
    String title,

    @NotBlank(message = "Body is required")
    @Size(
        min = 1,
        max = 5000,
        message = "Body must be between 1 and 5000 characters"
    )
    String body

) {}