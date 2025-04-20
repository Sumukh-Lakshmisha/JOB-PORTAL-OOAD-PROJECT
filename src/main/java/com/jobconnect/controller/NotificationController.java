package com.jobconnect.controller;

import com.jobconnect.model.CommunityPost;
import com.jobconnect.model.Notification;
import com.jobconnect.model.User;
import com.jobconnect.service.CommunityPostService;
import com.jobconnect.service.NotificationService;
import com.jobconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CommunityPostService communityPostService;
    
    @GetMapping
    public String getNotifications(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<Notification> notifications = notificationService.getUserNotifications(user.getId());
                model.addAttribute("notifications", notifications);
                model.addAttribute("user", user);
                
                // Add unread notification count for the header
                model.addAttribute("unreadNotifications", 
                    notificationService.getUnreadCount(user.getId()));
                
                return "notifications/list";
            }
        }
        return "redirect:/auth/login";
    }
    
    @GetMapping("/view/{id}")
    public String viewNotificationSource(@PathVariable String id) {
        Optional<Notification> notificationOpt = notificationService.getNotificationById(id);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            
            // Mark as read
            notificationService.markAsRead(id);
            
            if ("POST".equals(notification.getSourceType())) {
                // For post notifications, we need to find the community ID to redirect
                Optional<CommunityPost> postOpt = communityPostService.getPostById(notification.getSourceId());
                if (postOpt.isPresent()) {
                    CommunityPost post = postOpt.get();
                    return "redirect:/posts/" + post.getCommunityId() + "#post-" + notification.getSourceId();
                }
            } else if ("COMMUNITY".equals(notification.getSourceType())) {
                // For community notifications, redirect directly to the community
                return "redirect:/posts/" + notification.getSourceId();
            }
        }
        
        // Fallback to notifications list
        return "redirect:/notifications";
    }
    
    @PostMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                notificationService.markAllAsRead(user.getId());
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                long count = notificationService.getUnreadCount(user.getId());
                return ResponseEntity.ok(count);
            }
        }
        return ResponseEntity.ok(0L);
    }
}