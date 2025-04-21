package com.group02.mindmingle.dto.challenge;

import com.group02.mindmingle.model.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChallengeRequest {
    private String title;
    private String description;
    private Integer duration;
    private LocalDate startTime;
    private LocalDate endTime;
    private Challenge.ChallengeStatus status;
    private List<CreateChallengeDayRequest> challengeDays;
    private String imageUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChallengeDayRequest {
        private Long gameId;
        private Integer dayNumber;
    }
}