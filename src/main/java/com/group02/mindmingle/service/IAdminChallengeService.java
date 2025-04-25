package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.CreateChallengeRequest;

/**
 * 管理员挑战服务接口 - 负责管理员对挑战的CRUD操作
 */
public interface IAdminChallengeService {
    // 创建挑战
    ChallengeDto createChallenge(CreateChallengeRequest request);

    // 更新挑战
    ChallengeDto updateChallenge(Long challengeId, CreateChallengeRequest request);

    // 删除挑战
    void deleteChallenge(Long challengeId);
}