package com.group02.mindmingle.service.impl;

import com.group02.mindmingle.dto.PostUpdateRequest;
import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.model.PostSegment;
import com.group02.mindmingle.repository.CommunityPostRepository;
import com.group02.mindmingle.repository.PostSegmentRepository;
import com.group02.mindmingle.service.CommunityPostService;
import com.group02.mindmingle.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CommunityPostServiceImpl implements CommunityPostService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private PostSegmentRepository segmentRepository;

    @Autowired
    private FileUploadService fileUploadService;

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

    @Override
    @Transactional
    public CommunityPost updatePostContent(Long postId, String title, MultipartFile coverImageFile,
            boolean removeCoverImage, List<PostSegment> segments,
            Map<String, MultipartFile> segmentImages) throws Exception {

        // 获取帖子
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // 更新标题
        if (title != null && !title.trim().isEmpty()) {
            post.setTitle(title);
        }

        // 处理封面图片
        if (removeCoverImage) {
            // 如果指定了删除封面图片
            if (post.getImageUrl() != null) {
                // 可以选择从存储中删除原图片
                try {
                    fileUploadService.deleteFile(post.getImageUrl());
                } catch (Exception e) {
                    // 记录错误但不中断流程
                    System.err.println("Error deleting cover image: " + e.getMessage());
                }
            }
            post.setImageUrl(null);
        } else if (coverImageFile != null && !coverImageFile.isEmpty()) {
            // 上传新的封面图片
            try {
                String imageUrl = fileUploadService.uploadFile(coverImageFile, "my_post_images");

                // 如果有旧图片，可以删除
                if (post.getImageUrl() != null) {
                    try {
                        fileUploadService.deleteFile(post.getImageUrl());
                    } catch (Exception e) {
                        System.err.println("Error deleting old cover image: " + e.getMessage());
                    }
                }

                post.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload cover image", e);
            }
        }

        // 清除现有片段
        if (post.getSegments() != null) {
            segmentRepository.deleteByPostId(post.getId());
            post.getSegments().clear();
        }

        // 添加新片段
        if (segments != null && !segments.isEmpty()) {
            for (PostSegment segment : segments) {
                // 处理图片类型片段
                if (segment.getType() == PostSegment.SegmentType.IMAGE && segmentImages != null) {
                    String tempImageId = segment.getImageUrl(); // 暂时用imageUrl存储临时ID
                    MultipartFile imageFile = segmentImages.get(tempImageId);

                    if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                            // 上传图片
                            String imageUrl = fileUploadService.uploadFile(imageFile, "my_post_images");
                            segment.setImageUrl(imageUrl);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to upload segment image", e);
                        }
                    }
                }

                segment.setPost(post);
                post.getSegments().add(segment);
            }
        }

        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public CommunityPost updatePostWithRequest(Long postId, PostUpdateRequest request,
            MultipartFile coverImageFile,
            Map<String, MultipartFile> segmentImages) throws Exception {

        // 获取帖子
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // 更新标题
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            post.setTitle(request.getTitle());
        }

        // 处理封面图片
        if (request.isRemoveCoverImage()) {
            // 如果指定了删除封面图片
            if (post.getImageUrl() != null) {
                try {
                    fileUploadService.deleteFile(post.getImageUrl());
                } catch (Exception e) {
                    System.err.println("Error deleting cover image: " + e.getMessage());
                }
            }
            post.setImageUrl(null);
        } else if (coverImageFile != null && !coverImageFile.isEmpty()) {
            // 上传新封面
            try {
                String imageUrl = fileUploadService.uploadFile(coverImageFile, "my_post_images");

                // 如果有旧图片，可以删除
                if (post.getImageUrl() != null) {
                    try {
                        fileUploadService.deleteFile(post.getImageUrl());
                    } catch (Exception e) {
                        System.err.println("Error deleting old cover image: " + e.getMessage());
                    }
                }

                post.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload cover image", e);
            }
        }

        // 清除现有片段
        if (post.getSegments() != null) {
            // 清除前保存现有图片URL以便检查哪些需要删除
            List<String> oldImageUrls = new ArrayList<>();
            List<String> keptImageUrls = new ArrayList<>();

            for (PostSegment segment : post.getSegments()) {
                if (segment.getType() == PostSegment.SegmentType.IMAGE && segment.getImageUrl() != null) {
                    oldImageUrls.add(segment.getImageUrl());
                }
            }

            segmentRepository.deleteByPostId(post.getId());
            post.getSegments().clear();

            // 处理新片段
            if (request.getSegments() != null) {
                for (int i = 0; i < request.getSegments().size(); i++) {
                    var segmentDTO = request.getSegments().get(i);
                    PostSegment segment = new PostSegment();
                    segment.setType(segmentDTO.getType());
                    segment.setOrderIndex(i); // 使用列表索引作为排序索引

                    if (segmentDTO.getType() == PostSegment.SegmentType.TEXT) {
                        segment.setTextContent(segmentDTO.getTextContent());
                    } else if (segmentDTO.getType() == PostSegment.SegmentType.IMAGE) {
                        // 处理图片
                        if (segmentDTO.getTempImageId() != null && segmentImages != null) {
                            // 新上传的图片
                            MultipartFile imageFile = segmentImages.get(segmentDTO.getTempImageId());
                            if (imageFile != null && !imageFile.isEmpty()) {
                                try {
                                    String imageUrl = fileUploadService.uploadFile(imageFile, "my_post_images");
                                    segment.setImageUrl(imageUrl);
                                } catch (IOException e) {
                                    throw new RuntimeException("Failed to upload segment image", e);
                                }
                            }
                        } else if (segmentDTO.getImageUrl() != null) {
                            // 保留的现有图片
                            segment.setImageUrl(segmentDTO.getImageUrl());
                            keptImageUrls.add(segmentDTO.getImageUrl());
                        }
                    }

                    segment.setPost(post);
                    post.getSegments().add(segment);
                }
            }

            // 删除不再使用的图片
            for (String url : oldImageUrls) {
                if (!keptImageUrls.contains(url)) {
                    try {
                        fileUploadService.deleteFile(url);
                    } catch (Exception e) {
                        System.err.println("Error deleting unused segment image: " + e.getMessage());
                    }
                }
            }
        }

        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
}