package com.group02.mindmingle.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeProgressDto {
    private Long challengeId;
    private Integer currentDay;
    private List<Integer> completedDays;
    private String status;

    // 兼容前端数据结构的方法
    public static ChallengeProgressDto fromChallengeParticipation(
            com.group02.mindmingle.model.ChallengeParticipation participation) {
        return ChallengeProgressDto.builder()
                .challengeId(participation.getChallenge().getId())
                .currentDay(participation.getCurrentDay())
                .completedDays(participation.getCompletedDays() != null
                        ? participation.getCompletedDays()
                        : new ArrayList<>())
                .status(participation.getStatus().toString().toLowerCase())
                .build();
    }
}