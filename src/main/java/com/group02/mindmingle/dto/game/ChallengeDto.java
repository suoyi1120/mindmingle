package com.group02.mindmingle.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String imageUrl;
    private String createdAt; // 作为格式化的字符串
}