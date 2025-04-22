package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.CreateChallengeRequest;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeParticipation;
// import com.group02.mindmingle.model.DailyTask;

import java.util.List;

public interface ChallengeService {
    // 用户挑战相关
    List<Challenge> getAllChallenges();

    // 根据状态获取挑战(直接返回Challenge列表)
    List<Challenge> getChallengesByStatusModel(Challenge.ChallengeStatus status);

    void joinChallenge(Long challengeId, Long userId);

    List<ChallengeParticipation> getUserChallengeHistory(Long userId);

    // 管理员挑战管理相关
    ChallengeDto createChallenge(CreateChallengeRequest request);

    ChallengeDto updateChallenge(Long challengeId, CreateChallengeRequest request);

    ChallengeDto getChallengeById(Long challengeId);

    List<ChallengeDto> getAllChallengesForAdmin();

    List<ChallengeDto> getChallengesByStatus(Challenge.ChallengeStatus status);

    // 获取默认挑战列表（优先返回ACTIVE状态的挑战）
    List<ChallengeDto> getDefaultChallenges(String status);

    void deleteChallenge(Long challengeId);

    // 定时任务相关
    void updateChallengeStatuses();
}
