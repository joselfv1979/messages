package com.messageapp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messageapp.dto.MessageRequest;
import com.messageapp.model.Message;
import com.messageapp.model.User;
import com.messageapp.openapi.annotation.NotFoundResponse;
import com.messageapp.openapi.annotation.UnauthorizedResponse;
import com.messageapp.openapi.annotation.ValidationErrorResponse;
import com.messageapp.security.AuthenticatedUserService;
import com.messageapp.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(
    name = "Messages",
    description = "Operations for managing user messages"
)
@SecurityRequirement(name = "bearerAuth")
public class MessageController {
    
    private final MessageService messageService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @Operation(
        summary = "Get all messages",
        description = "Returns all messages belonging to the authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Messages retrieved successfully"
    )
    @UnauthorizedResponse
    public ResponseEntity<?> getAll() {

        User currentUser = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                messageService.getAllByUser(currentUser.getId()));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get message by id",
        description = "Returns a specific message owned by the authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message retrieved successfully"
    )
    @UnauthorizedResponse
    @NotFoundResponse
    public ResponseEntity<Message> getById(
        @PathVariable String id,
        @RequestHeader("Authorization") String authHeader) {

        User currentUser = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
            messageService.getById(id, currentUser.getId()));
    }

    @PostMapping
    @Operation(
        summary = "Create message",
        description = "Creates a new message for the authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message created successfully"
    )
    @ValidationErrorResponse
    @UnauthorizedResponse
    public ResponseEntity<?> create(
        @Valid @RequestBody MessageRequest request, 
        @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = authenticatedUserService.getCurrentUser();
        
        return ResponseEntity.ok(
            messageService.create(request, currentUser.getId()));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update message",
        description = "Updates an existing message owned by the authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message updated successfully"
    )
    @ValidationErrorResponse
    @UnauthorizedResponse
    @NotFoundResponse
    public ResponseEntity<?> update(@PathVariable String id, 
        @Valid @RequestBody MessageRequest request, 
        @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = authenticatedUserService.getCurrentUser();
        
        return ResponseEntity.ok(
            messageService.update(id, request, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete message",
        description = "Deletes a message owned by the authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Message deleted successfully"
    )
    @UnauthorizedResponse
    @NotFoundResponse
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = authenticatedUserService.getCurrentUser();
        
        messageService.delete(id, currentUser.getId());
        
        return ResponseEntity.ok(Map.of("message", "Message deleted"));
    }

}
