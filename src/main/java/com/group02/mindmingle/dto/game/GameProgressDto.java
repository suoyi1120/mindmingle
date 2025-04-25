package com.group02.mindmingle.dto.game;

import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameProgressDto {
    private Long gameId;
    private Integer day;
    private String title;
    private String description;
    private String storage_url;
    private boolean completed;

    public static GameProgressDto fromChallengeDayDto(ChallengeDayDto challengeDayDto, boolean completed) {
        return GameProgressDto.builder()
                .gameId(challengeDayDto.getGameId())
                .day(challengeDayDto.getDayNumber())
                .title(challengeDayDto.getGameTitle())
                .description(challengeDayDto.getDescription())
                .storage_url(challengeDayDto.getStorageUrl())
                .completed(completed)
                .build();
    }
}