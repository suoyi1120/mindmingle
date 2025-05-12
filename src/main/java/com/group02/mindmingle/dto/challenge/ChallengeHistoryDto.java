package com.group02.mindmingle.dto.challenge;

import com.group02.mindmingle.model.ChallengeParticipation;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeHistoryDto {
    private Long id;
    private Long challengeId;
    private String challengeTitle;
    private String challengeDescription;
    private String challengeImageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ChallengeParticipation.Status status;
    private Integer currentDay;
    private List<Integer> completedDays;
    private String username;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    public static ChallengeHistoryDto fromChallengeParticipation(ChallengeParticipation participation) {
        return ChallengeHistoryDto.builder()
                .id(participation.getId())
                .challengeId(participation.getChallenge().getId())
                .challengeTitle(participation.getChallenge().getTitle())
                .challengeDescription(participation.getChallenge().getDescription())
                .challengeImageUrl(participation.getChallenge().getImageUrl())
                .startDate(participation.getStartDate())
                .endDate(participation.getEndDate())
                .status(participation.getStatus())
                .currentDay(participation.getCurrentDay())
                .completedDays(participation.getCompletedDays())
                .username(participation.getUser().getUsername())
                .userEmail(participation.getUser().getEmail())
                .userFirstName(participation.getUser().getFirstName())
                .userLastName(participation.getUser().getLastName())
                .build();
    }
} 