package com.jobconnect.service;

import com.jobconnect.model.CommunityPost;
import com.jobconnect.model.PostLike;
import com.jobconnect.model.User;
import com.jobconnect.repository.CommunityPostRepository;
import com.jobconnect.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private PostLikeRepository likeRepository;
    
    @Autowired
    private CommunityPostRepository postRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired 
    private UserService userService;

    public PostLike likePost(String userId, String postId) {
        // Check if already liked
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            return likeRepository.findByUserIdAndPostId(userId, postId).orElse(null);
        }
        
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setPostId(postId);
        like.setCreatedAt(LocalDateTime.now());
        
        // Save the like
        PostLike savedLike = likeRepository.save(like);
        
        // Create notification for post author
        Optional<CommunityPost> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            CommunityPost post = postOptional.get();
            
            // Only create notification if the post author is not the same as the liker
            if (!post.getAuthorUserId().equals(userId)) {
                Optional<User> likerOptional = userService.findById(userId);
                if (likerOptional.isPresent()) {
                    User liker = likerOptional.get();
                    String message = liker.getName() + " liked your post";
                    notificationService.createNotification(
                        post.getAuthorUserId(), 
                        "NEW_LIKE", 
                        message, 
                        postId, 
                        "POST"
                    );
                }
            }
        }
        
        return savedLike;
    }
    
    public void unlikePost(String userId, String postId) {
        Optional<PostLike> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);
        existingLike.ifPresent(like -> likeRepository.delete(like));
    }
    
    public boolean isLiked(String userId, String postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
    
    public long getLikeCount(String postId) {
        return likeRepository.countByPostId(postId);
    }
}