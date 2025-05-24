package com.example.telegram.controller;

import com.example.telegram.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
@Tag(name = "Notifications", description = "Endpoints for sending Telegram notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Send notification message")
    public ResponseEntity<Void> sendNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Notification text")
            @RequestBody String message) {
        notificationService.sendNotification(message);
        return ResponseEntity.ok().build();
    }
}
