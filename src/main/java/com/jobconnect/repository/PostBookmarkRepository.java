package com.jobconnect.repository;

import com.jobconnect.model.PostBookmark;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostBookmarkRepository extends MongoRepository<PostBookmark, String> {
    List<PostBookmark> findByUserId(String userId);
    List<PostBookmark> findByUserIdAndCommunityId(String userId, String communityId);
    Optional<PostBookmark> findByUserIdAndPostId(String userId, String postId);
    void deleteByUserIdAndPostId(String userId, String postId);
    boolean existsByUserIdAndPostId(String userId, String postId);
}