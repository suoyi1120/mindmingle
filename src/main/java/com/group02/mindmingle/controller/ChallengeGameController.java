package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.ChallengeGame;
import com.group02.mindmingle.service.ChallengeGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge-games")
public class ChallengeGameController {

    private final ChallengeGameService challengeGameService;

    @Autowired
    public ChallengeGameController(ChallengeGameService challengeGameService) {
        this.challengeGameService = challengeGameService;
    }

    @PostMapping
    public ResponseEntity<ChallengeGame> createChallengeGame(@RequestBody ChallengeGame challengeGame) {
        ChallengeGame createdGame = challengeGameService.createChallengeGame(challengeGame);
        return ResponseEntity.ok(createdGame);
    }

    @GetMapping
    public ResponseEntity<List<ChallengeGame>> getAllChallengeGames() {
        List<ChallengeGame> games = challengeGameService.getAllChallengeGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeGame> getChallengeGameById(@PathVariable Long id) {
        return challengeGameService.getChallengeGameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<ChallengeGame>> getChallengeGamesByChallengeId(
            @PathVariable Integer challengeId) {
        List<ChallengeGame> games = challengeGameService.getChallengeGamesByChallengeId(challengeId);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ChallengeGame>> getChallengeGamesByGameId(
            @PathVariable Integer gameId) {
        List<ChallengeGame> games = challengeGameService.getChallengeGamesByGameId(gameId);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/challenge/{challengeId}/day/{day}")
    public ResponseEntity<ChallengeGame> getChallengeGameByDayNumber(
            @PathVariable Integer challengeId,
            @PathVariable Integer day) {
        return challengeGameService.getChallengeGameByDayNumber(challengeId, day)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeGame> updateChallengeGame(
            @PathVariable Long id,
            @RequestBody ChallengeGame challengeGame) {
        ChallengeGame updatedGame = challengeGameService.updateChallengeGame(id, challengeGame);
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallengeGame(@PathVariable Long id) {
        challengeGameService.deleteChallengeGame(id);
        return ResponseEntity.ok().build();
    }
} 