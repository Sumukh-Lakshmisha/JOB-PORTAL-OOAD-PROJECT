package com.jobconnect.service;

import com.jobconnect.model.CommunityPost;
import com.jobconnect.model.PostBookmark;
import com.jobconnect.repository.CommunityPostRepository;
import com.jobconnect.repository.PostBookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    @Autowired
    private PostBookmarkRepository bookmarkRepository;
    
    @Autowired
    private CommunityPostRepository postRepository;

    public PostBookmark bookmarkPost(String userId, String postId, String communityId) {
        // Check if already bookmarked
        if (bookmarkRepository.existsByUserIdAndPostId(userId, postId)) {
            return bookmarkRepository.findByUserIdAndPostId(userId, postId).orElse(null);
        }
        
        PostBookmark bookmark = new PostBookmark();
        bookmark.setUserId(userId);
        bookmark.setPostId(postId);
        bookmark.setCommunityId(communityId);
        
        return bookmarkRepository.save(bookmark);
    }
    
    public void removeBookmark(String userId, String postId) {
        Optional<PostBookmark> existingBookmark = bookmarkRepository.findByUserIdAndPostId(userId, postId);
        existingBookmark.ifPresent(bookmark -> bookmarkRepository.delete(bookmark));
    }
    
    public boolean isBookmarked(String userId, String postId) {
        return bookmarkRepository.existsByUserIdAndPostId(userId, postId);
    }
    
    public List<CommunityPost> getBookmarkedPosts(String userId) {
        List<PostBookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        List<String> postIds = bookmarks.stream()
                                       .map(PostBookmark::getPostId)
                                       .collect(Collectors.toList());
        
        return postRepository.findAllById(postIds);
    }
    
    public List<CommunityPost> getBookmarkedPostsByCommunity(String userId, String communityId) {
        List<PostBookmark> bookmarks = bookmarkRepository.findByUserIdAndCommunityId(userId, communityId);
        List<String> postIds = bookmarks.stream()
                                       .map(PostBookmark::getPostId)
                                       .collect(Collectors.toList());
        
        return postRepository.findAllById(postIds);
    }
}