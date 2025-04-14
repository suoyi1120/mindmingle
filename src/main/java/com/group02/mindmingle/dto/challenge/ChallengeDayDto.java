package com.group02.mindmingle.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDayDto {
    private Long id;
    private Long challengeId;
    private Long gameId;
    private String gameTitle; // 前端显示用
    private Integer dayNumber;
}