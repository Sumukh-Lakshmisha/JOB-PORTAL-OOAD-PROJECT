package com.jobconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "communities")
@Data
public class Community {

    @Id
    private String id;
    private String name;
    private String description;
    private String adminUserId; // User who created/admins the community
    private List<String> memberUserIds = new ArrayList<>(); // Initialize the list
    
    // Add a method to check if a user is a member
    public boolean isMember(String userId) {
        return memberUserIds.contains(userId);
    }
}