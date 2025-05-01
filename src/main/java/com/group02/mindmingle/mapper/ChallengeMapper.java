package com.group02.mindmingle.mapper;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeDay;
import com.group02.mindmingle.model.ChallengeParticipation;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
                .userStatus("ACTIVE") // 默认状态是ACTIVE
                .build();
    }

    /**
     * 将Challenge实体转换为ChallengeDto，并包含用户参与状态
     */
    public ChallengeDto mapToChallengeDto(Challenge challenge, Optional<ChallengeParticipation> participation) {
        ChallengeDto dto = mapToChallengeDto(challenge);

        if (participation.isPresent()) {
            // 如果用户有参与记录，设置对应的状态
            dto.setUserStatus(participation.get().getStatus().name());
        } else {
            // 如果用户没有参与记录，设置为"ACTIVE"(未参与)
            dto.setUserStatus("ACTIVE");
        }

        return dto;
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