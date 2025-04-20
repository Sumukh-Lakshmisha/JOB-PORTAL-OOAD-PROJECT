package com.jobconnect.service;

import com.jobconnect.model.Community;
import com.jobconnect.model.CommunityPost;
import com.jobconnect.model.User;
import com.jobconnect.repository.CommunityPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityPostService {

    @Autowired
    private CommunityPostRepository communityPostRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public CommunityPost createCommunityPost(String communityId, String authorUserId, String content) {
        CommunityPost post = new CommunityPost();
        post.setCommunityId(communityId);
        post.setAuthorUserId(authorUserId);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        
        // Get user's name
        Optional<User> userOptional = userService.findById(authorUserId);
        String authorName = "Unknown User";
        if (userOptional.isPresent()) {
            authorName = userOptional.get().getName();
            post.setAuthorName(authorName);
        } else {
            post.setAuthorName(authorName);
        }
        
        CommunityPost savedPost = communityPostRepository.save(post);
        
        // Notify community members via WebSocket
        messagingTemplate.convertAndSend("/topic/community/" + communityId, savedPost);
        
        // Get community to access member list
        Community community = communityService.getCommunity(communityId);
        
        // Create notifications for all community members except the post author
        notificationService.createNewPostNotifications(communityId, authorUserId, savedPost.getId(), authorName, community);
        
        return savedPost;
    }
    
    public List<CommunityPost> getCommunityPosts(String communityId) {
        return communityPostRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }

    // Add this method to your CommunityPostService class
    public Optional<CommunityPost> getPostById(String id) {
        return communityPostRepository.findById(id);
    }
}