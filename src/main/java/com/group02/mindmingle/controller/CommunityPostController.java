package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.service.CommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityPostController {

    @Autowired
    private CommunityPostService postService;

    @PostMapping("/posts")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPost> createPost(@RequestBody CommunityPost post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPost>> getAllVisiblePosts() {
        return ResponseEntity.ok(postService.getAllVisiblePosts());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<CommunityPost> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/posts/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPost> updatePost(@PathVariable Long id, @RequestBody CommunityPost post) {
        return ResponseEntity.ok(postService.updatePost(id, post));
    }

    @DeleteMapping("/posts/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
