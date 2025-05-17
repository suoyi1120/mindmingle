package com.group02.mindmingle.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommunityPostDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPinned;
    private boolean isVisible;
    private int likes;
    private boolean isLike = false;

    // 添加内容片段列表
    private List<PostSegmentDTO> segments = new ArrayList<>();
}
