package com.messageapp.service;

import com.messageapp.dto.MessageRequest;
import com.messageapp.model.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final Map<Long, Message> messages = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Message> getAllByUser(Long userId) {
        return messages.values().stream()
                .filter(m -> m.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public Message getById(Long id, Long userId) {
        Message message = messages.get(id);
        if (message == null || !message.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Message not found");
        }
        return message;
    }

    public Message create(MessageRequest request, Long userId) {
        Message message = new Message(idCounter.getAndIncrement(), request.getTitle(), request.getBody(), userId);
        messages.put(message.getId(), message);
        return message;
    }

    public Message update(Long id, MessageRequest request, Long userId) {
        Message message = messages.get(id);
        if (message == null || !message.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Message not found");
        }
        message.setTitle(request.getTitle());
        message.setBody(request.getBody());
        message.setUpdatedAt(LocalDateTime.now());
        return message;
    }

    public void delete(Long id, Long userId) {
        Message message = messages.get(id);
        if (message == null || !message.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Message not found");
        }
        messages.remove(id);
    }
}
