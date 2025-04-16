package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.PostDTO;
import com.group02.mindmingle.dto.PostCreateDTO;
import com.group02.mindmingle.service.CommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommunityPostController {

    @Autowired
    private CommunityPostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping(value = "/posts", consumes = {"multipart/form-data"})
    public ResponseEntity<PostDTO> createPost(
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        
        PostCreateDTO postDTO = new PostCreateDTO();
        postDTO.setTitle(title);
        postDTO.setDescription(description);
        
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.createPost(postDTO, image, userId));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<PostDTO> likePost(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.likePost(id, userId));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
