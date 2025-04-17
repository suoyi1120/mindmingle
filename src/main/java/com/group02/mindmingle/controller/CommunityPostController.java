package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.service.CommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityPostController {

    @Autowired
    private CommunityPostService postService;

    // Add a community post
    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPost> createPost(@RequestBody CommunityPost post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    // Check all community posts
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPost>> getAllVisiblePosts() {
        return ResponseEntity.ok(postService.getAllVisiblePosts());
    }

    // check a post by id
    @GetMapping("/posts/{id}")
    public ResponseEntity<CommunityPost> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // edit a post by id
    @PutMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPost> updatePost(@PathVariable Long id, @RequestBody CommunityPost post) {
        return ResponseEntity.ok(postService.updatePost(id, post));
    }

    //delete a post by id
    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
