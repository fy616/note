package com.campustrash.controller;

import com.campustrash.dto.MessageRequest;
import com.campustrash.entity.Message;
import com.campustrash.security.JwtUser;
import com.campustrash.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/{orderId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String orderId) {
        return ResponseEntity.ok(messageService.getMessages(orderId, user.getId()));
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String orderId,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(orderId, user.getId(), request));
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String orderId) {
        long count = messageService.getUnreadCount(orderId, user.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
