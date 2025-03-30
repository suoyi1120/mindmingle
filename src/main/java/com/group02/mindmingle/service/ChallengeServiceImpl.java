package com.group02.mindmingle.service;

import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.ChallengeParticipationRepository;
import com.group02.mindmingle.repository.ChallengeRepository;
import com.group02.mindmingle.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @Override
    public void joinChallenge(Long challengeId, Long userId) {
        Optional<ChallengeParticipation> existingParticipation =
                participationRepository.findByUser_IdAndChallenge_ChallengesId(userId, challengeId);

        if (existingParticipation.isPresent()) {
            return; 
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChallengeParticipation participation = new ChallengeParticipation();
        participation.setChallenge(challenge);
        participation.setUser(user);
        participationRepository.save(participation);
    }

    @Override
    public List<ChallengeParticipation> getUserChallengeHistory(Long userId) {
        return participationRepository.findByUser_Id(userId);
    }
}
