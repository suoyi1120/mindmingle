package com.group02.mindmingle.mapper;

import com.group02.mindmingle.dto.CommunityPostDTO;
import com.group02.mindmingle.model.CommunityPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommunityPostMapper {

    @Autowired
    private PostSegmentMapper segmentMapper;

    public CommunityPostDTO toDTO(CommunityPost post) {
        if (post == null) {
            return null;
        }

        CommunityPostDTO dto = new CommunityPostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setAuthorId(post.getAuthor() != null ? post.getAuthor().getId() : null);
        dto.setAuthorName(post.getAuthor() != null ? post.getAuthor().getUsername() : null);
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setPinned(post.isPinned());
        dto.setVisible(post.isVisible());
        dto.setLikes(post.getLikes());
        dto.setLike(false); // 默认设置为 false，后续可以根据用户状态修改

        // 映射内容片段列表
        if (post.getSegments() != null && !post.getSegments().isEmpty()) {
            dto.setSegments(segmentMapper.toDTOList(post.getSegments()));
        }

        return dto;
    }
}
