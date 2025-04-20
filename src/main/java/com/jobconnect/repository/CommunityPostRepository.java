package com.jobconnect.repository;

import com.jobconnect.model.CommunityPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends MongoRepository<CommunityPost, String> {
    List<CommunityPost> findByCommunityIdOrderByCreatedAtDesc(String communityId);
}