package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.CreateChallengeRequest;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.service.IAdminChallengeService;
import com.group02.mindmingle.service.IChallengeQueryService;
import com.group02.mindmingle.scheduler.ChallengeStatusScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/challenge")
@PreAuthorize("hasRole('ADMIN')")
public class ChallengeManagementController {

    @Autowired
    private IAdminChallengeService adminChallengeService;

    @Autowired
    private IChallengeQueryService challengeQueryService;

    @Autowired
    private ChallengeStatusScheduler challengeStatusScheduler;

    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllChallenges(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            try {
                Challenge.ChallengeStatus challengeStatus = Challenge.ChallengeStatus.valueOf(status.toUpperCase());
                return ResponseEntity.ok(challengeQueryService.getChallengesByStatus(challengeStatus));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(challengeQueryService.getAllChallengesForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeQueryService.getChallengeById(id));
    }

    @PostMapping
    public ResponseEntity<ChallengeDto> createChallenge(@RequestBody CreateChallengeRequest request) {
        ChallengeDto createdChallenge = adminChallengeService.createChallenge(request);
        return new ResponseEntity<>(createdChallenge, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDto> updateChallenge(
            @PathVariable Long id,
            @RequestBody CreateChallengeRequest request) {
        return ResponseEntity.ok(adminChallengeService.updateChallenge(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        adminChallengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    // 以下为手动触发状态更新的端点，通常用于测试或特殊情况
    @PostMapping("/update-statuses")
    public ResponseEntity<Void> updateChallengeStatuses() {
        challengeStatusScheduler.updateChallengeStatuses();
        return ResponseEntity.ok().build();
    }
}
