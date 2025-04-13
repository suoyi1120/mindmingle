package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.ChallengeGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeGameRepository extends JpaRepository<ChallengeGame, Long> {
    List<ChallengeGame> findByChallengeId(Integer challengeId);
    List<ChallengeGame> findByGameId(Integer gameId);
    Optional<ChallengeGame> findByChallengeIdAndDay(Integer challengeId, Integer day);
} 