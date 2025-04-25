package com.group02.mindmingle.mapper;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeDay;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 挑战实体与DTO映射组件
 */
@Component
public class ChallengeMapper {

    /**
     * 将Challenge实体转换为ChallengeDto
     */
    public ChallengeDto mapToChallengeDto(Challenge challenge) {
        List<ChallengeDayDto> challengeDayDtos = challenge.getChallengeDays().stream()
                .map(this::mapToChallengeDayDto)
                .collect(Collectors.toList());

        return ChallengeDto.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .duration(challenge.getDuration())
                .challengeStatus(challenge.getStatus())
                .startTime(challenge.getStartTime())
                .endTime(challenge.getEndTime())
                .createdAt(challenge.getCreatedAt())
                .challengeDays(challengeDayDtos)
                .imageUrl(challenge.getImageUrl())
                .build();
    }

    /**
     * 将ChallengeDay实体转换为ChallengeDayDto
     */
    public ChallengeDayDto mapToChallengeDayDto(ChallengeDay challengeDay) {
        return ChallengeDayDto.builder()
                .id(challengeDay.getId())
                .challengeId(challengeDay.getChallenge().getId())
                .gameId(challengeDay.getGame().getId())
                .gameTitle(challengeDay.getGame().getTitle())
                .dayNumber(challengeDay.getDayNumber())
                .description(challengeDay.getGame().getDescription())
                .storageUrl(challengeDay.getGame().getStorageUrl())
                .build();
    }
}