package com.jobconnect.repository;

import com.jobconnect.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends MongoRepository<Community, String> {
    // Find communities where the user is a member
    List<Community> findByMemberUserIdsContaining(String userId);
}