package com.jobconnect.service;

import com.jobconnect.model.Community;
// import com.jobconnect.model.CommunityPost;
import com.jobconnect.model.Notification;
import com.jobconnect.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(String userId, String type, String message, String sourceId, String sourceType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setSourceId(sourceId);
        notification.setSourceType(sourceType);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
        return notificationRepository.save(notification);
    }
    
    public void createNewPostNotifications(String communityId, String postAuthorId, String postId, String authorName, Community community) {
        // Create notifications for all community members except the post author
        for (String memberId : community.getMemberUserIds()) {
            if (!memberId.equals(postAuthorId)) {
                String message = authorName + " posted in " + community.getName();
                createNotification(
                    memberId,
                    "NEW_POST",
                    message,
                    postId,
                    "POST"
                );
            }
        }
    }
    
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
    }
    
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }
    
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    
    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    // Add this method to your NotificationService class
    public Optional<Notification> getNotificationById(String id) {
        return notificationRepository.findById(id);
    }

    
}