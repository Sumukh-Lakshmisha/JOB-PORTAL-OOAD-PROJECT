package com.jobconnect.controller;

import com.jobconnect.model.Community;
import com.jobconnect.model.CommunityPost;
import com.jobconnect.model.User;
import com.jobconnect.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class CommunityPostController {

    @Autowired
    private CommunityPostService communityPostService;
    
    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookmarkService bookmarkService;
    
    @Autowired
    private LikeService likeService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{communityId}")
    public String getCommunityPosts(@PathVariable String communityId, Model model) {
        // Get community details
        Community community = communityService.getCommunity(communityId);
        model.addAttribute("community", community);
        
        // Get posts for this community
        List<CommunityPost> posts = communityPostService.getCommunityPosts(communityId);
        model.addAttribute("posts", posts);
        model.addAttribute("communityId", communityId);
        
        // Add current user to model for checking if user is a member
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("userId", user.getId());
                model.addAttribute("isMember", community.getMemberUserIds().contains(user.getId()));
                
                // Add bookmark information for each post
                Map<String, Boolean> bookmarkStatus = new HashMap<>();
                // Add like information for each post
                Map<String, Boolean> likeStatus = new HashMap<>();
                // Add like counts for each post
                Map<String, Long> likeCounts = new HashMap<>();
                
                for (CommunityPost post : posts) {
                    bookmarkStatus.put(post.getId(), 
                        bookmarkService.isBookmarked(user.getId(), post.getId()));
                    
                    likeStatus.put(post.getId(),
                        likeService.isLiked(user.getId(), post.getId()));
                    
                    likeCounts.put(post.getId(), 
                        likeService.getLikeCount(post.getId()));
                }
                
                model.addAttribute("bookmarkStatus", bookmarkStatus);
                model.addAttribute("likeStatus", likeStatus);
                model.addAttribute("likeCounts", likeCounts);
                
                // Add notification count
                model.addAttribute("unreadNotifications", 
                    notificationService.getUnreadCount(user.getId()));
            }
        }
        
        return "communities/posts";
    }

    @PostMapping("/create/{communityId}")
    public String createCommunityPost(@PathVariable String communityId, @RequestParam String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Check if user is a member of this community
                Community community = communityService.getCommunity(communityId);
                if (community.getMemberUserIds().contains(user.getId())) {
                    communityPostService.createCommunityPost(communityId, user.getId(), content);
                }
            }
        }
        return "redirect:/posts/" + communityId;
    }
    
    @PostMapping("/bookmark/{postId}")
    public String bookmarkPost(@PathVariable String postId, @RequestParam String communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                bookmarkService.bookmarkPost(user.getId(), postId, communityId);
            }
        }
        return "redirect:/posts/" + communityId;
    }
    
    @PostMapping("/unbookmark/{postId}")
    public String unbookmarkPost(@PathVariable String postId, @RequestParam String communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                bookmarkService.removeBookmark(user.getId(), postId);
            }
        }
        return "redirect:/posts/" + communityId;
    }
    
    @PostMapping("/like/{postId}")
    public String likePost(@PathVariable String postId, @RequestParam String communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                likeService.likePost(user.getId(), postId);
            }
        }
        return "redirect:/posts/" + communityId;
    }
    
    @PostMapping("/unlike/{postId}")
    public String unlikePost(@PathVariable String postId, @RequestParam String communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                likeService.unlikePost(user.getId(), postId);
            }
        }
        return "redirect:/posts/" + communityId;
    }
    
    @GetMapping("/bookmarks")
    public String getBookmarkedPosts(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<CommunityPost> bookmarkedPosts = bookmarkService.getBookmarkedPosts(user.getId());
                model.addAttribute("posts", bookmarkedPosts);
                model.addAttribute("user", user);
                
                // Add notification count
                model.addAttribute("unreadNotifications", 
                    notificationService.getUnreadCount(user.getId()));
                    
                return "communities/bookmarks";
            }
        }
        return "redirect:/auth/login";
    }
}