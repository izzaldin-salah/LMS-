package com.example.demo.services;

import com.example.demo.repository.NotificationRepository;
import com.example.demo.tables.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public Notification sendNotification(Long recipientId, String message) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(Long recipientId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsRead(recipientId, false);

        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    
        return unreadNotifications; 
    }

    public List<Notification> getAllNotifications(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }


    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
