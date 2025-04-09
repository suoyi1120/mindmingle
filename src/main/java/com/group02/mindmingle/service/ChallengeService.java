package com.group02.mindmingle.service;

import com.group02.mindmingle.model.Challenge;

import com.group02.mindmingle.model.ChallengeParticipation;
// import com.group02.mindmingle.model.DailyTask;

import java.util.List;

public interface ChallengeService {
    List<Challenge> getAllChallenges();
    void joinChallenge(Long challengeId, Long userId);
    List<ChallengeParticipation> getUserChallengeHistory(Long userId);
}

