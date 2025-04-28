package com.group02.mindmingle.service;

import com.group02.mindmingle.model.CommunityPost;

import java.util.List;
import java.util.Optional;

public interface CommunityPostService {
    CommunityPost createPost(CommunityPost post);
    List<CommunityPost> getAllVisiblePosts();
    Optional<CommunityPost> getPostById(Long id);
    CommunityPost updatePost(Long id, CommunityPost updatedPost);
    void deletePost(Long id);
    List<CommunityPost> getPostsByUser(Long userId);
    CommunityPost likePost(Long id);
}