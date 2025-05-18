package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.Reward;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.RewardService;
import com.group02.mindmingle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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
    public ResponseEntity<List<RewardDTO>> getAllRewards() {
        List<Reward> rewards = rewardService.getAllRewards();
        List<RewardDTO> rewardDTOS = new ArrayList<>();
        for(Reward reward :rewards){
            RewardDTO rewardDTO = new RewardDTO();
            rewardDTO.id = reward.getId();
            rewardDTO.challengeTitle = reward.getChallenge().getTitle();
            rewardDTO.description = reward.getDescription();
            rewardDTO.name = reward.getName();
            rewardDTO.iconUrl = reward.getIconUrl();
            rewardDTO.createdAt = reward.getCreatedAt();
            rewardDTOS.add(rewardDTO);
        }
        return ResponseEntity.ok(rewardDTOS);
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

    @GetMapping("/my-rewards")
    public ResponseEntity<?> getRewardsByCurrentUser() {
        try {
            List<RewardDTO> rewardDTOS = new ArrayList<>();
            Set<Reward> rewards = rewardService.getRewardsByCurrentUser();
            for(Reward reward :rewards){
                RewardDTO rewardDTO = new RewardDTO();
                rewardDTO.id = reward.getId();
                rewardDTO.challengeTitle = reward.getChallenge().getTitle();
                rewardDTO.description = reward.getDescription();
                rewardDTO.name = reward.getName();
                rewardDTO.iconUrl = reward.getIconUrl();
                rewardDTO.createdAt = reward.getCreatedAt();
                rewardDTOS.add(rewardDTO);
            }
            return ResponseEntity.ok(rewardDTOS);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    public static class RewardDTO implements Serializable {
        private Long id;
        private String name;
        private String description;
        private String iconUrl;
        private String challengeTitle;

        private LocalDateTime createdAt;

        public RewardDTO() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getChallengeTitle() {
            return challengeTitle;
        }

        public void setChallengeTitle(String challengeTitle) {
            this.challengeTitle = challengeTitle;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

