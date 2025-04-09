package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping
    public List<Challenge> listAllChallenges() {
        return challengeService.getAllChallenges();
    }

    @PostMapping("/join/{challengeId}")
    public ResponseEntity<String> joinChallenge(@PathVariable Long challengeId, @RequestBody Long userId) {
        challengeService.joinChallenge(challengeId, userId);
        return ResponseEntity.ok("Joined challenge successfully");
    }

    @GetMapping("/history/{userId}")
    public List<ChallengeParticipation> getHistory(@PathVariable Long userId) {
        return challengeService.getUserChallengeHistory(userId);
    }
}
