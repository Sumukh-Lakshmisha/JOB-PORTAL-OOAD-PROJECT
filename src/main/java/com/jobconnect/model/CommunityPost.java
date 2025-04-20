package com.jobconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "community_posts")
@Data
public class CommunityPost {

    @Id
    private String id;
    private String communityId;
    private String authorUserId;
    private String authorName; // Added to store author's name
    private String content;
    private LocalDateTime createdAt;
}