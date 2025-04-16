package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.PostDTO;
import com.group02.mindmingle.dto.PostCreateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityPostService {
    List<PostDTO> getAllPosts();
    PostDTO createPost(PostCreateDTO postDTO, MultipartFile image, Long userId);
    PostDTO getPostById(Long id);
    PostDTO likePost(Long postId, Long userId);
    void deletePost(Long id);
    List<PostDTO> getPostsByUser(Long userId);
}