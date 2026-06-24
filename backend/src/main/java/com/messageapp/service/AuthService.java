package com.messageapp.service;

import com.messageapp.dto.AuthResponse;
import com.messageapp.dto.LoginRequest;
import com.messageapp.dto.RegisterRequest;
import com.messageapp.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> tokens = new ConcurrentHashMap<>();
    private final Map<String, User> usernames = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @PostConstruct
    public void seedDefaults() {
        User jose = new User(idCounter.getAndIncrement(), "jose", "renaido");
        String token = UUID.randomUUID().toString();
        jose.setToken(token);
        users.put(jose.getId(), jose);
        usernames.put(jose.getUsername(), jose);
        tokens.put(token, jose);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usernames.containsKey(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        User user = new User(idCounter.getAndIncrement(), request.getUsername(), request.getPassword());
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        users.put(user.getId(), user);
        usernames.put(user.getUsername(), user);
        tokens.put(token, user);
        return new AuthResponse(user.getId(), user.getUsername(), token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = usernames.get(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        tokens.put(token, user);
        return new AuthResponse(user.getId(), user.getUsername(), token);
    }

    public User validateToken(String token) {
        if (token == null || token.isEmpty()) return null;
        User user = tokens.get(token);
        if (user == null || !token.equals(user.getToken())) return null;
        return user;
    }
}
