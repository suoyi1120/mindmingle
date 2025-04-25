package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.challenge.ChallengeProgressDto;
import com.group02.mindmingle.model.ChallengeParticipation;

import java.util.List;

/**
 * 用户挑战服务接口 - 负责用户与挑战的交互
 */
public interface IUserChallengeService {
    // 用户加入挑战
    void joinChallenge(Long challengeId, Long userId);

    // 获取用户挑战历史
    List<ChallengeParticipation> getUserChallengeHistory(Long userId);

    // 获取用户的挑战进度
    ChallengeProgressDto getUserChallengeProgress(Long challengeId, Long userId);

    // 开始一个新挑战
    ChallengeProgressDto startChallenge(Long challengeId, Long userId);

    // 完成某一天的游戏挑战
    boolean completeDailyGame(Long challengeId, Long userId, Integer day);
}