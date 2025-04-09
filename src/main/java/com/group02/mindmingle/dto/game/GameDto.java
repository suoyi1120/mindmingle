package com.group02.mindmingle.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String imageUrl;
    private String storageUrl;
    private String createdAt; // 作为格式化的字符串
}
