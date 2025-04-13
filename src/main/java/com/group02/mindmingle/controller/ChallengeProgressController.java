package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.ChallengeProgress;
import com.group02.mindmingle.service.ChallengeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge-progress")
public class ChallengeProgressController {

    private final ChallengeProgressService challengeProgressService;

    @Autowired
    public ChallengeProgressController(ChallengeProgressService challengeProgressService) {
        this.challengeProgressService = challengeProgressService;
    }

    @PostMapping
    public ResponseEntity<ChallengeProgress> createChallengeProgress(@RequestBody ChallengeProgress challengeProgress) {
        ChallengeProgress createdProgress = challengeProgressService.createChallengeProgress(challengeProgress);
        return ResponseEntity.ok(createdProgress);
    }

    @GetMapping
    public ResponseEntity<List<ChallengeProgress>> getAllChallengeProgress() {
        List<ChallengeProgress> progressList = challengeProgressService.getAllChallengeProgress();
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/{progressId}")
    public ResponseEntity<ChallengeProgress> getChallengeProgressById(@PathVariable Long progressId) {
        return challengeProgressService.getChallengeProgressById(progressId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<ChallengeProgress>> getChallengeProgressByChallengeId(
            @PathVariable Integer challengeId) {
        List<ChallengeProgress> progressList = challengeProgressService.getChallengeProgressByChallengeId(challengeId);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeProgress>> getChallengeProgressByUserId(
            @PathVariable Integer userId) {
        List<ChallengeProgress> progressList = challengeProgressService.getChallengeProgressByUserId(userId);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/challenge/{challengeId}/user/{userId}")
    public ResponseEntity<ChallengeProgress> getChallengeProgressByChallengeAndUser(
            @PathVariable Integer challengeId,
            @PathVariable Integer userId) {
        return challengeProgressService.getChallengeProgressByChallengeAndUser(challengeId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChallengeProgress>> getChallengeProgressByStatus(
            @PathVariable String status) {
        List<ChallengeProgress> progressList = challengeProgressService.getChallengeProgressByStatus(status);
        return ResponseEntity.ok(progressList);
    }

    @PutMapping("/{progressId}")
    public ResponseEntity<ChallengeProgress> updateChallengeProgress(
            @PathVariable Long progressId,
            @RequestBody ChallengeProgress challengeProgress) {
        ChallengeProgress updatedProgress = challengeProgressService.updateChallengeProgress(progressId, challengeProgress);
        return ResponseEntity.ok(updatedProgress);
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<Void> deleteChallengeProgress(@PathVariable Long progressId) {
        challengeProgressService.deleteChallengeProgress(progressId);
        return ResponseEntity.ok().build();
    }
} 