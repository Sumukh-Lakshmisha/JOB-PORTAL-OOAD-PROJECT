package com.jobconnect.repository;

import com.jobconnect.model.PostLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends MongoRepository<PostLike, String> {
    List<PostLike> findByPostId(String postId);
    Optional<PostLike> findByUserIdAndPostId(String userId, String postId);
    boolean existsByUserIdAndPostId(String userId, String postId);
    void deleteByUserIdAndPostId(String userId, String postId);
    long countByPostId(String postId);
}