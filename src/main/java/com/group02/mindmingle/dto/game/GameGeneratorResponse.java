package com.group02.mindmingle.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 游戏生成响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameGeneratorResponse {
    private Long gameId;
    private String title;
    private String storageUrl;
}