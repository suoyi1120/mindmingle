package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.PostUpdateRequest;
import com.group02.mindmingle.model.CommunityPost;
import com.group02.mindmingle.model.PostSegment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommunityPostService {
    CommunityPost createPost(CommunityPost post);

    List<CommunityPost> getAllVisiblePosts();

    Optional<CommunityPost> getPostById(Long id);

    CommunityPost updatePost(Long id, CommunityPost updatedPost);

    void deletePost(Long id);

    List<CommunityPost> getPostsByUser(Long userId);

    CommunityPost likePost(Long id);

    // 添加新的方法用于更新帖子内容
    CommunityPost updatePostContent(Long postId, String title, MultipartFile coverImageFile,
            boolean removeCoverImage, List<PostSegment> segments,
            Map<String, MultipartFile> segmentImages) throws Exception;

    // 接收使用JSON和文件的复杂更新
    CommunityPost updatePostWithRequest(Long postId, PostUpdateRequest request,
            MultipartFile coverImageFile,
            Map<String, MultipartFile> segmentImages) throws Exception;
}