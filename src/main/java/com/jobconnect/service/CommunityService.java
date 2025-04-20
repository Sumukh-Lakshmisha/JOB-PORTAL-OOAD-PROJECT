package com.jobconnect.service;

import com.jobconnect.model.Community;
import com.jobconnect.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    public Community createCommunity(Community community) {
        // Ensure member list is initialized
        if (community.getMemberUserIds() == null) {
            throw new IllegalArgumentException("Member list must be initialized");
        }
        return communityRepository.save(community);
    }

    public Community joinCommunity(String communityId, String userId) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new RuntimeException("Community not found"));
        
        if (!community.getMemberUserIds().contains(userId)) {
            community.getMemberUserIds().add(userId);
            return communityRepository.save(community);
        }
        return community;
    }

    public Community leaveCommunity(String communityId, String userId) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new RuntimeException("Community not found"));
        
        // Cannot leave if you're the admin
        if (community.getAdminUserId().equals(userId)) {
            throw new RuntimeException("Admin cannot leave the community");
        }
        
        community.getMemberUserIds().remove(userId);
        return communityRepository.save(community);
    }

    public List<Community> listCommunities() {
        return communityRepository.findAll();
    }
    
    public List<Community> getUserCommunities(String userId) {
        return communityRepository.findByMemberUserIdsContaining(userId);
    }
    
    public Community getCommunity(String communityId) {
        return communityRepository.findById(communityId)
            .orElseThrow(() -> new RuntimeException("Community not found"));
    }
}