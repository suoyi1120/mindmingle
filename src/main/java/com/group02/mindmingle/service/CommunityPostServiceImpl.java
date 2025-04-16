package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.PostDTO;
import com.group02.mindmingle.dto.PostCreateDTO;
import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.CommunityPostRepository;
import com.group02.mindmingle.repository.UserRepository;
// import com.group02.mindmingle.service.CommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityPostServiceImpl implements CommunityPostService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private final String uploadDir = "uploads/";

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO createPost(PostCreateDTO postDTO, MultipartFile image, Long userId) {
        CommunityPost post = new CommunityPost();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setAuthor(userRepository.findById(userId).orElseThrow());
        post.setLikes(0);

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = saveImage(image);
                post.setImageUrl("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image", e);
            }
        }

        CommunityPost savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    @Override
    public PostDTO getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public PostDTO likePost(Long postId, Long userId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setLikes(post.getLikes() + 1);
        CommunityPost updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<PostDTO> getPostsByUser(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath);
        
        return fileName;
    }

    private PostDTO convertToDTO(CommunityPost post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId().toString());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setImageUrl(post.getImageUrl());
        dto.setLikes(post.getLikes());
        dto.setLiked(false);
        
        User author = post.getAuthor();
        if (author != null) {
            dto.setAuthor(author.getUsername());
            dto.setAvatar(null);
        } else {
            dto.setAuthor("Anonymous");
            dto.setAvatar(null);
        }
        
        dto.setTime(formatTime(post.getCreatedAt()));
        return dto;
    }

    private String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}