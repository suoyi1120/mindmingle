package com.group02.mindmingle.dto;

import lombok.Data;
import java.time.LocalDateTime;

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
}
