package com.jobconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "post_likes")
@Data
public class PostLike {
    @Id
    private String id;
    private String userId;
    private String postId;
    private LocalDateTime createdAt;
}