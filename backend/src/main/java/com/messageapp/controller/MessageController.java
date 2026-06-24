package com.messageapp.controller;

import com.messageapp.dto.MessageRequest;
import com.messageapp.model.Message;
import com.messageapp.model.User;
import com.messageapp.service.AuthService;
import com.messageapp.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    private final AuthService authService;

    public MessageController(MessageService messageService, AuthService authService) {
        this.messageService = messageService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String authHeader) {
        User user = validateUser(authHeader);
        if (user == null) return unauthorized();
        List<Message> messages = messageService.getAllByUser(user.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        User user = validateUser(authHeader);
        if (user == null) return unauthorized();
        try {
            Message message = messageService.getById(id, user.getId());
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MessageRequest request, @RequestHeader("Authorization") String authHeader) {
        User user = validateUser(authHeader);
        if (user == null) return unauthorized();
        Message message = messageService.create(request, user.getId());
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MessageRequest request, @RequestHeader("Authorization") String authHeader) {
        User user = validateUser(authHeader);
        if (user == null) return unauthorized();
        try {
            Message message = messageService.update(id, request, user.getId());
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        User user = validateUser(authHeader);
        if (user == null) return unauthorized();
        try {
            messageService.delete(id, user.getId());
            return ResponseEntity.ok(Map.of("message", "Message deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private User validateUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return authService.validateToken(token);
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }
}
