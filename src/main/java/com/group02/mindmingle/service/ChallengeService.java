package com.group02.mindmingle.service;

import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Autowired
    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Optional<Challenge> getChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId);
    }

    public List<Challenge> getChallengesByUserId(Long userId) {
        return challengeRepository.findByUser_Id(userId);
    }

    public Optional<Challenge> getChallengeByTitle(String title) {
        return challengeRepository.findByTitle(title);
    }

    public List<Challenge> getChallengesByTimeRange(LocalDateTime start, LocalDateTime end) {
        return challengeRepository.findByStartTimeBetween(start, end);
    }

    public List<Challenge> getChallengesByDuration(Integer duration) {
        return challengeRepository.findByDuration(duration);
    }

    public Challenge createChallenge(Challenge challenge) {
        challenge.setCreatedAt(LocalDateTime.now());
        return challengeRepository.save(challenge);
    }

    public Challenge updateChallenge(Long challengeId, Challenge challengeDetails) {
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    challenge.setTitle(challengeDetails.getTitle());
                    challenge.setDescription(challengeDetails.getDescription());
                    challenge.setDuration(challengeDetails.getDuration());
                    challenge.setStartTime(challengeDetails.getStartTime());
                    challenge.setEndTime(challengeDetails.getEndTime());
                    challenge.setBackgroundImage(challengeDetails.getBackgroundImage());
                    return challengeRepository.save(challenge);
                })
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));
    }

    public void deleteChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }
}

