package com.group02.mindmingle.service;

import com.group02.mindmingle.model.ChallengeGame;
import com.group02.mindmingle.repository.ChallengeGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeGameService {

    private final ChallengeGameRepository challengeGameRepository;

    @Autowired
    public ChallengeGameService(ChallengeGameRepository challengeGameRepository) {
        this.challengeGameRepository = challengeGameRepository;
    }

    public List<ChallengeGame> getAllChallengeGames() {
        return challengeGameRepository.findAll();
    }

    public Optional<ChallengeGame> getChallengeGameById(Long id) {
        return challengeGameRepository.findById(id);
    }

    public List<ChallengeGame> getChallengeGamesByChallengeId(Integer challengeId) {
        return challengeGameRepository.findByChallengeId(challengeId);
    }

    public List<ChallengeGame> getChallengeGamesByGameId(Integer gameId) {
        return challengeGameRepository.findByGameId(gameId);
    }

    public Optional<ChallengeGame> getChallengeGameByDayNumber(Integer challengeId, Integer day) {
        return challengeGameRepository.findByChallengeIdAndDay(challengeId, day);
    }

    public ChallengeGame createChallengeGame(ChallengeGame challengeGame) {
        return challengeGameRepository.save(challengeGame);
    }

    public ChallengeGame updateChallengeGame(Long id, ChallengeGame challengeGameDetails) {
        return challengeGameRepository.findById(id)
                .map(game -> {
                    game.setChallengeId(challengeGameDetails.getChallengeId());
                    game.setGameId(challengeGameDetails.getGameId());
                    game.setDay(challengeGameDetails.getDay());
                    return challengeGameRepository.save(game);
                })
                .orElseThrow(() -> new RuntimeException("Challenge game not found with id: " + id));
    }

    public void deleteChallengeGame(Long id) {
        challengeGameRepository.deleteById(id);
    }
} 