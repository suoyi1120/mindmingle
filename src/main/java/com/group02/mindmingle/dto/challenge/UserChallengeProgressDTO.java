package com.group02.mindmingle.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeProgressDTO {
    private Long id; // 挑战ID
    private String title; // 挑战标题
    private String description; // 挑战描述
    private Integer duration; // 挑战总天数
    private Integer progress; // 当前进度(已完成天数)
    private Integer currentDay; // 当前天数
    private String currentGameTitle; // 当前游戏标题
    private String imageUrl; // 挑战图片URL
}