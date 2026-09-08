package com.messageapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.messageapp.dto.MessageRequest;
import com.messageapp.dto.MessageResponse;
import com.messageapp.exception.ResourceNotFoundException;
import com.messageapp.model.Message;
import com.messageapp.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<MessageResponse> getAllByUser(String userId) {
        return messageRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    public MessageResponse getById(String id, String userId) {
        Message message = getOwnedMessage(id, userId);
        return MessageResponse.from(message);
    }

    public MessageResponse create(MessageRequest request, String userId) {

        Message message = Message.builder()
                .title(request.title())
                .body(request.body())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return MessageResponse.from(messageRepository.save(message));
    }

    public MessageResponse update(String id, MessageRequest request, String userId) {

        Message message = getOwnedMessage(id, userId);

        message.setTitle(request.title());
        message.setBody(request.body());
        message.setUpdatedAt(LocalDateTime.now());

        return MessageResponse.from(messageRepository.save(message));
    }

    public void delete(String id, String userId) {

        Message message = getOwnedMessage(id, userId);
        messageRepository.delete(message);
    }

    private Message getOwnedMessage(String id, String userId) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Message not found");
        }

        return message;
    }
}
