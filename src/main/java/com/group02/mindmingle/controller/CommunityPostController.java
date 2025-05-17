package com.group02.mindmingle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group02.mindmingle.dto.CommunityPostDTO;
import com.group02.mindmingle.dto.PostUpdateRequest;
import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.service.CommunityPostService;
import com.group02.mindmingle.mapper.CommunityPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.group02.mindmingle.service.FileUploadService;
import com.group02.mindmingle.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CommunityPostController {

    @Autowired
    private CommunityPostService postService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CommunityPostMapper postMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // Add a community post
    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPostDTO> createPost(
            @RequestParam("title") String title,
            @RequestParam(name = "coverImageFile", required = false) MultipartFile coverImageFile,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommunityPost post = new CommunityPost();
        post.setTitle(title);
        post.setAuthor(currentUser);
        post.setVisible(true); // Default to visible
        post.setPinned(false); // Default to not pinned
        post.setLikes(0); // Default likes
        // post.setContent(""); // Initialize content as empty for now, or handle as
        // needed

        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            try {
                String imageUrl = fileUploadService.uploadFile(coverImageFile, "my_post_images");
                post.setImageUrl(imageUrl);
            } catch (IOException e) {
                // Consider logging the error and returning a specific error response
                System.err.println("File upload failed for post title '" + title + "': " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        // createdAt and updatedAt are set by the service
        CommunityPost savedPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDTO(savedPost)); // Use 201 CREATED
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

    // 更新帖子内容（支持文件上传）
    @PutMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPostDTO> updatePostContent(
            @PathVariable Long id,
            @RequestParam(value = "postData") String postDataJson,
            @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
            @RequestParam(required = false) Map<String, MultipartFile> files) {

        try {
            // 解析JSON数据
            PostUpdateRequest updateRequest = objectMapper.readValue(postDataJson, PostUpdateRequest.class);

            // 准备图片文件映射
            Map<String, MultipartFile> segmentImages = new HashMap<>();
            if (files != null) {
                // 从请求参数中提取segment_image_前缀开头的文件
                for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                    if (entry.getKey().startsWith("segment_image_")) {
                        segmentImages.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            // 调用服务层进行更新
            CommunityPost updatedPost = postService.updatePostWithRequest(
                    id, updateRequest, coverImageFile, segmentImages);

            return ResponseEntity.ok(postMapper.toDTO(updatedPost));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // delete a post by id
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
