package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.CommunityPostDTO;
import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.service.CommunityPostService;
import com.group02.mindmingle.mapper.CommunityPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class CommunityPostController {


   @Autowired
   private CommunityPostService postService;


   @Autowired
   private CommunityPostMapper postMapper;


   // Add a community post
   @PostMapping("/posts")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<CommunityPostDTO> createPost(@ModelAttribute CommunityPost post) {
       CommunityPost savedPost = postService.createPost(post);
       return ResponseEntity.ok(postMapper.toDTO(savedPost));
   }


   // Check all community posts
   @GetMapping("/posts")
   public ResponseEntity<List<CommunityPostDTO>> getAllVisiblePosts() {
       List<CommunityPost> posts = postService.getAllVisiblePosts();
       List<CommunityPostDTO> dtos = posts.stream()
               .map(postMapper::toDTO)
               .collect(Collectors.toList());
       return ResponseEntity.ok(dtos);
   }


   // check a post by id
   @GetMapping("/posts/{id}")
   public ResponseEntity<CommunityPostDTO> getPostById(@PathVariable Long id) {
       return postService.getPostById(id)
               .map(post -> ResponseEntity.ok(postMapper.toDTO(post)))
               .orElse(ResponseEntity.notFound().build());
   }


   // edit a post by id
   @PutMapping("/posts/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<CommunityPostDTO> updatePost(@PathVariable Long id, @RequestBody CommunityPost post) {
       CommunityPost updatedPost = postService.updatePost(id, post);
       return ResponseEntity.ok(postMapper.toDTO(updatedPost));
   }


   //delete a post by id
   @DeleteMapping("/posts/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Void> deletePost(@PathVariable Long id) {
       postService.deletePost(id);
       return ResponseEntity.noContent().build();
   }


   // Like a post
   @PostMapping("/posts/{id}/like")
   public ResponseEntity<CommunityPostDTO> likePost(@PathVariable Long id) {
       CommunityPost likedPost = postService.likePost(id);
       CommunityPostDTO dto = postMapper.toDTO(likedPost);
       dto.setLike(true); // 设置当前用户已点赞
       return ResponseEntity.ok(dto);
   }
}
