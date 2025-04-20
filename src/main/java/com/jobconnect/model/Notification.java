package com.jobconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    private String id;
    private String userId; // Who should receive this notification
    private String type; // e.g., "NEW_POST", "NEW_LIKE"
    private String message;
    private String sourceId; // postId, communityId, etc.
    private String sourceType; // "POST", "COMMUNITY", etc.
    private LocalDateTime createdAt;
    private boolean read = false;
}