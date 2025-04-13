package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) {
        Challenge createdChallenge = challengeService.createChallenge(challenge);
        return ResponseEntity.ok(createdChallenge);
    }

    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable Long challengeId) {
        return challengeService.getChallengeById(challengeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Challenge>> getChallengesByUserId(@PathVariable Long userId) {
        List<Challenge> challenges = challengeService.getChallengesByUserId(userId);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Challenge> getChallengeByTitle(@PathVariable String title) {
        return challengeService.getChallengeByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/timerange")
    public ResponseEntity<List<Challenge>> getChallengesByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Challenge> challenges = challengeService.getChallengesByTimeRange(start, end);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/duration/{duration}")
    public ResponseEntity<List<Challenge>> getChallengesByDuration(@PathVariable Integer duration) {
        List<Challenge> challenges = challengeService.getChallengesByDuration(duration);
        return ResponseEntity.ok(challenges);
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<Challenge> updateChallenge(
            @PathVariable Long challengeId,
            @RequestBody Challenge challenge) {
        Challenge updatedChallenge = challengeService.updateChallenge(challengeId, challenge);
        return ResponseEntity.ok(updatedChallenge);
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long challengeId) {
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.ok().build();
    }
}
