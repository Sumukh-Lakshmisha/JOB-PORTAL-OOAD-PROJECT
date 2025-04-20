package com.jobconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post_bookmarks")
@Data
public class PostBookmark {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String communityId; // For easier filtering
}