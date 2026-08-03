package com.messageapp.openapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.messageapp.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "404",
    description = "Resource not found",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(
            implementation = ErrorResponse.class
        )
    )
)
public @interface NotFoundResponse {
}