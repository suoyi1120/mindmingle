package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.dto.challenge.ChallengeProgressDto;
import com.group02.mindmingle.dto.game.GameProgressDto;
import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.IChallengeQueryService;
import com.group02.mindmingle.service.IUserChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    @Autowired
    private IChallengeQueryService challengeQueryService;

    @Autowired
    private IUserChallengeService userChallengeService;

    @GetMapping
    public List<ChallengeDto> listAllChallenges(@RequestParam(required = false) String status) {
        // 使用查询服务获取挑战列表
        return challengeQueryService.getDefaultChallenges(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeQueryService.getChallengeById(id));
    }

    @PostMapping("/join/{challengeId}")
    public ResponseEntity<String> joinChallenge(@PathVariable Long challengeId, @RequestBody Long userId) {
        userChallengeService.joinChallenge(challengeId, userId);
        return ResponseEntity.ok("Joined challenge successfully");
    }

    @GetMapping("/history/{userId}")
    public List<ChallengeParticipation> getHistory(@PathVariable Long userId) {
        return userChallengeService.getUserChallengeHistory(userId);
    }

    // 获取挑战的每日游戏列表
    @GetMapping("/{challengeId}/daily-games")
    public ResponseEntity<List<ChallengeDayDto>> getChallengeDailyGames(@PathVariable Long challengeId) {
        List<ChallengeDayDto> dailyGames = challengeQueryService.getChallengeDailyGames(challengeId);
        return ResponseEntity.ok(dailyGames);
    }

    // 获取特定日期的游戏内容
    @GetMapping("/{challengeId}/day/{day}")
    public ResponseEntity<GameProgressDto> getDailyGame(
            @PathVariable Long challengeId,
            @PathVariable Integer day,
            Authentication authentication) {
        // 从Authentication对象获取当前用户信息(如果已登录)
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            userId = currentUser.getId();
        }

        // 获取游戏信息和完成状态
        GameProgressDto dailyGame = challengeQueryService.getDailyGameWithProgress(challengeId, day, userId);
        return ResponseEntity.ok(dailyGame);
    }

    // 获取用户的挑战进度
    @GetMapping("/{challengeId}/progress")
    public ResponseEntity<ChallengeProgressDto> getUserChallengeProgress(
            @PathVariable Long challengeId,
            Authentication authentication) {
        // 从Authentication对象中获取当前用户信息
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getId();

        ChallengeProgressDto progress = userChallengeService.getUserChallengeProgress(challengeId, userId);
        return ResponseEntity.ok(progress);
    }

    // 开始一个新挑战
    @PostMapping("/{challengeId}/start")
    public ResponseEntity<ChallengeProgressDto> startChallenge(
            @PathVariable Long challengeId,
            Authentication authentication) {
        // 从Authentication对象中获取当前用户信息
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getId();

        ChallengeProgressDto progress = userChallengeService.startChallenge(challengeId, userId);
        return ResponseEntity.ok(progress);
    }

    // 完成某一天的游戏挑战
    @PostMapping("/{challengeId}/complete-day")
    public ResponseEntity<Map<String, Object>> completeDailyGame(
            @PathVariable Long challengeId,
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {
        // 从Authentication对象中获取当前用户信息
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getId();

        Integer day = request.get("day");

        boolean completed = userChallengeService.completeDailyGame(challengeId, userId, day);
        return ResponseEntity.ok(Map.of("success", completed));
    }
}
