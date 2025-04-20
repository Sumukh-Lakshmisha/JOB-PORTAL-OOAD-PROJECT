package com.jobconnect.controller;

import com.jobconnect.model.Community;
import com.jobconnect.model.User;
import com.jobconnect.service.CommunityService;
import com.jobconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String listCommunities(Model model) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                
                String userId = user.getId();
                List<Community> allCommunities = communityService.listCommunities();
                List<Community> userCommunities = communityService.getUserCommunities(userId);
                
                model.addAttribute("communities", allCommunities);
                model.addAttribute("userCommunities", userCommunities);
                model.addAttribute("userId", userId);
                
                return "communities/list";
            }
        }
        
        // If not authenticated or user not found, just show all communities
        List<Community> communities = communityService.listCommunities();
        model.addAttribute("communities", communities);
        return "communities/list";
    }

    @GetMapping("/my")
    public String myCommunitiesList(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                
                String userId = user.getId();
                List<Community> userCommunities = communityService.getUserCommunities(userId);
                
                model.addAttribute("communities", userCommunities);
                model.addAttribute("userId", userId);
                return "communities/list";
            }
        }
        return "redirect:/auth/login"; // Redirect to login if not authenticated
    }

    @PostMapping("/create")
    public String createCommunity(@ModelAttribute Community community) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String userId = user.getId();
                
                community.setAdminUserId(userId);
                if (community.getMemberUserIds() == null) {
                    community.getMemberUserIds().add(userId); // Admin is also a member
                } else if (!community.getMemberUserIds().contains(userId)) {
                    community.getMemberUserIds().add(userId);
                }
                
                communityService.createCommunity(community);
                return "redirect:/communities";
            }
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/join/{communityId}")
    public String joinCommunity(@PathVariable String communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                communityService.joinCommunity(communityId, user.getId());
                return "redirect:/communities";
            }
        }
        return "redirect:/auth/login";
    }
    
    @PostMapping("/leave/{communityId}")
    public String leaveCommunity(@PathVariable String communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOptional = userService.findByEmail(auth.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                communityService.leaveCommunity(communityId, user.getId());
                return "redirect:/communities";
            }
        }
        return "redirect:/auth/login";
    }
}