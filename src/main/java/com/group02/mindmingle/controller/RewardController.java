package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.Reward;
import com.group02.mindmingle.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping
    public ResponseEntity<Reward> createReward(@RequestBody Reward reward) {
        Reward createdReward = rewardService.createReward(reward);
        return ResponseEntity.ok(createdReward);
    }

    @GetMapping
    public ResponseEntity<List<Reward>> getAllRewards() {
        List<Reward> rewards = rewardService.getAllRewards();
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reward> getRewardById(@PathVariable Long id) {
        return rewardService.getRewardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<Reward>> getRewardsByChallengeId(@PathVariable Integer challengeId) {
        List<Reward> rewards = rewardService.getRewardsByChallengeId(challengeId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Reward> getRewardByName(@PathVariable String name) {
        return rewardService.getRewardByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reward> updateReward(@PathVariable Long id, @RequestBody Reward reward) {
        Reward updatedReward = rewardService.updateReward(id, reward);
        return ResponseEntity.ok(updatedReward);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/challenge/{challengeId}")
    public ResponseEntity<Boolean> checkChallengeRewardExists(@PathVariable Integer challengeId) {
        return ResponseEntity.ok(rewardService.existsByChallengeId(challengeId));
    }
} 