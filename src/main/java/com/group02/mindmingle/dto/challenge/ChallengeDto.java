package com.group02.mindmingle.dto.challenge;

import com.group02.mindmingle.model.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDto {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private Challenge.ChallengeStatus challengeStatus;
    private LocalDate startTime;
    private LocalDate endTime;
    private LocalDateTime createdAt;
    private List<ChallengeDayDto> challengeDays;
    private String imageUrl;
}