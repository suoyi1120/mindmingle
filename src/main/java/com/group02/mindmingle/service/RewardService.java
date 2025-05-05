package com.group02.mindmingle.service;

import com.group02.mindmingle.model.Reward;
import com.group02.mindmingle.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    @Autowired
    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    public Optional<Reward> getRewardById(Long id) {
        return rewardRepository.findById(id);
    }

    public List<Reward> getRewardsByChallengeId(Integer challengeId) {
        return rewardRepository.findByChallengeId(challengeId);
    }

    public Optional<Reward> getRewardByName(String name) {
        return rewardRepository.findByName(name);
    }

    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    public Reward updateReward(Long id, Reward rewardDetails) {
        return rewardRepository.findById(id)
                .map(reward -> {
                    reward.setChallengeId(rewardDetails.getChallengeId());
                    reward.setName(rewardDetails.getName());
                    reward.setDescription(rewardDetails.getDescription());
                    reward.setIconUrl(rewardDetails.getIconUrl());
                    return rewardRepository.save(reward);
                })
                .orElseThrow(() -> new RuntimeException("Reward not found with id: " + id));
    }

    public void deleteReward(Long id) {
        rewardRepository.deleteById(id);
    }

    public boolean existsByChallengeId(Integer challengeId) {
        return rewardRepository.existsByChallengeId(challengeId);
    }
} 