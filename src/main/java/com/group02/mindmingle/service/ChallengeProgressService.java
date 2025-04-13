package com.group02.mindmingle.service;

import com.group02.mindmingle.model.ChallengeProgress;
import com.group02.mindmingle.repository.ChallengeProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeProgressService {

    private final ChallengeProgressRepository challengeProgressRepository;

    @Autowired
    public ChallengeProgressService(ChallengeProgressRepository challengeProgressRepository) {
        this.challengeProgressRepository = challengeProgressRepository;
    }

    public List<ChallengeProgress> getAllChallengeProgress() {
        return challengeProgressRepository.findAll();
    }

    public Optional<ChallengeProgress> getChallengeProgressById(Long progressId) {
        return challengeProgressRepository.findById(progressId);
    }

    public List<ChallengeProgress> getChallengeProgressByChallengeId(Integer challengeId) {
        return challengeProgressRepository.findByChallengeId(challengeId);
    }

    public List<ChallengeProgress> getChallengeProgressByUserId(Integer userId) {
        return challengeProgressRepository.findByUserId(userId);
    }

    public Optional<ChallengeProgress> getChallengeProgressByChallengeAndUser(Integer challengeId, Integer userId) {
        return challengeProgressRepository.findByChallengeIdAndUserId(challengeId, userId);
    }

    public List<ChallengeProgress> getChallengeProgressByStatus(String status) {
        return challengeProgressRepository.findByStatus(status);
    }

    public ChallengeProgress createChallengeProgress(ChallengeProgress challengeProgress) {
        return challengeProgressRepository.save(challengeProgress);
    }

    public ChallengeProgress updateChallengeProgress(Long progressId, ChallengeProgress progressDetails) {
        return challengeProgressRepository.findById(progressId)
                .map(progress -> {
                    progress.setCurrentDay(progressDetails.getCurrentDay());
                    progress.setCompletedDays(progressDetails.getCompletedDays());
                    progress.setStatus(progressDetails.getStatus());
                    if ("completed".equals(progressDetails.getStatus())) {
                        progress.setCompletedAt(LocalDateTime.now());
                    }
                    return challengeProgressRepository.save(progress);
                })
                .orElseThrow(() -> new RuntimeException("Challenge progress not found with id: " + progressId));
    }

    public void deleteChallengeProgress(Long progressId) {
        challengeProgressRepository.deleteById(progressId);
    }
} 