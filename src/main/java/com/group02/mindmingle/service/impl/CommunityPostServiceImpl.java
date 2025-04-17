package com.group02.mindmingle.service.impl;

import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.repository.CommunityPostRepository;
import com.group02.mindmingle.service.CommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommunityPostServiceImpl implements CommunityPostService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Override
    public CommunityPost createPost(CommunityPost post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public List<CommunityPost> getAllVisiblePosts() {
        return postRepository.findByIsVisibleTrueOrderByCreatedAtDesc();
    }

    @Override
    public Optional<CommunityPost> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public CommunityPost updatePost(Long id, CommunityPost updatedPost) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setImageUrl(updatedPost.getImageUrl());
                    post.setPinned(updatedPost.isPinned());
                    post.setVisible(updatedPost.isVisible());
                    post.setUpdatedAt(LocalDateTime.now());
                    return postRepository.save(post);
                })
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<CommunityPost> getPostsByUser(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    @Override
    public CommunityPost likePost(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    int currentLikes = post.getLikes();
                    post.setLikes(currentLikes + 1);
                    return postRepository.save(post);
                })
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }
} 