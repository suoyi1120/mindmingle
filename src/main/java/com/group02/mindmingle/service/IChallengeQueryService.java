package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.dto.game.GameProgressDto;
import com.group02.mindmingle.model.Challenge;

import java.util.List;

/**
 * 挑战查询服务接口 - 负责所有与挑战相关的查询操作
 */
public interface IChallengeQueryService {
    // 获取所有挑战
    List<Challenge> getAllChallengesModel();

    // 根据状态获取挑战(直接返回Challenge列表)
    List<Challenge> getChallengesByStatusModel(Challenge.ChallengeStatus status);

    // 获取挑战DTO
    ChallengeDto getChallengeById(Long challengeId);

    // 管理员相关查询
    List<ChallengeDto> getAllChallengesForAdmin();

    // 根据状态获取挑战DTO列表
    List<ChallengeDto> getChallengesByStatus(Challenge.ChallengeStatus status);

    // 获取默认挑战列表（优先返回ACTIVE状态的挑战）
    List<ChallengeDto> getDefaultChallenges(String status);

    // 获取挑战的每日游戏列表
    List<ChallengeDayDto> getChallengeDailyGames(Long challengeId);

    // 获取特定日期的游戏内容
    ChallengeDayDto getDailyGame(Long challengeId, Integer day);
}