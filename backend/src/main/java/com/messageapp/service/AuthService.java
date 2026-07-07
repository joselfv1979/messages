package com.messageapp.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.messageapp.dto.AuthResponse;
import com.messageapp.dto.LoginRequest;
import com.messageapp.dto.RegisterRequest;
import com.messageapp.exception.DuplicateUserException;
import com.messageapp.exception.UnauthorizedException;
import com.messageapp.model.User;
import com.messageapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateUserException("Username already taken");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .token(UUID.randomUUID().toString())
                .build();

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getToken()
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        user.setToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getToken()
        );
    }

    public User validateToken(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        return userRepository.findByToken(token).orElse(null);
    }
}