package com.group02.mindmingle.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDTO {
    private String id;
    private String author;
    private String avatar;
    private String time;
    private String title;
    private String description;
    private String imageUrl;
    private int likes;
    private boolean liked;
} 