package com.messageapp.dto;

import java.time.LocalDateTime;

import com.messageapp.model.Message;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponse(

    @Schema(
        description = "Message identifier",
        example = "687e4d8bfe3dcb1c7d123456"
    )
    String id,

    @Schema(
        description = "Message title",
        example = "Shopping List"
    )
    String title,

    @Schema(
        description = "Message content",
        example = "Milk, Bread and Coffee"
    )
    String body,

    @Schema(
        description = "Owner user identifier",
        example = "687e4d8bfe3dcb1c7d123456"
    )
    String userId,

    @Schema(
        description = "Message creation timestamp"
    )
    LocalDateTime createdAt,

    @Schema(
        description = "Message last update timestamp"
    )
    LocalDateTime updatedAt

) {

    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getTitle(),
                message.getBody(),
                message.getUserId(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
