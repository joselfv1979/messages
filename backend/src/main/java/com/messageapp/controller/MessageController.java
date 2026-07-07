package com.messageapp.controller;

import java.util.List;
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
import com.messageapp.exception.UnauthorizedException;
import com.messageapp.model.Message;
import com.messageapp.model.User;
import com.messageapp.service.AuthService;
import com.messageapp.service.MessageService;

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
    public ResponseEntity<Message> getById(
        @PathVariable String id,
        @RequestHeader("Authorization") String authHeader) {

        User user = validateUser(authHeader);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Message message = messageService.getById(id, user.getId());

        return ResponseEntity.ok(message);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MessageRequest request, 
        @RequestHeader("Authorization") String authHeader) {
        
        User user = validateUser(authHeader);
        
        if (user == null) return unauthorized();
        
        Message message = messageService.create(request, user.getId());
        
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody MessageRequest request, 
        @RequestHeader("Authorization") String authHeader) {
        
        User user = validateUser(authHeader);
        
        if (user == null) {
            throw new UnauthorizedException("Invalid or missing token");
        }

        Message message = messageService.update(id, request, user.getId());
        
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        User user = validateUser(authHeader);
        if (user == null) {
            return unauthorized();
        }
        messageService.delete(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "Message deleted"));
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
