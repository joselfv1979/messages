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
import com.messageapp.security.AuthenticatedUserService;
import com.messageapp.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    public ResponseEntity<?> getAll() {

        User currentUser = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                messageService.getAllByUser(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getById(
        @PathVariable String id,
        @RequestHeader("Authorization") String authHeader) {

        User currentUser = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
            messageService.getById(id, currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MessageRequest request, 
        @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = authenticatedUserService.getCurrentUser();
        
        return ResponseEntity.ok(
            messageService.create(request, currentUser.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody MessageRequest request, 
        @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = authenticatedUserService.getCurrentUser();
        
        return ResponseEntity.ok(
            messageService.update(id, request, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = authenticatedUserService.getCurrentUser();
        
        messageService.delete(id, currentUser.getId());
        
        return ResponseEntity.ok(Map.of("message", "Message deleted"));
    }

}
