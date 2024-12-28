package com.example.demo.controllers;

import com.example.demo.services.JwtUtil;
import com.example.demo.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import com.example.demo.tables.Notification;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil; // Inject JwtUtil as a dependency

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR','ADMIN')")
    public List<Notification> getUnread(HttpServletRequest request) {
        Long recipientId = jwtUtil.extractUserId(request); // Extract recipientId using JwtUtil
        return notificationService.getUnreadNotifications(recipientId);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR','ADMIN')")
    public List<Notification> getAll(HttpServletRequest request) {
        Long recipientId = jwtUtil.extractUserId(request); // Extract recipientId using JwtUtil
        return notificationService.getAllNotifications(recipientId);
    }

}
