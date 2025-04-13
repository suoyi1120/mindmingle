package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.ChallengeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeProgressRepository extends JpaRepository<ChallengeProgress, Long> {
    List<ChallengeProgress> findByChallengeId(Integer challengeId);
    List<ChallengeProgress> findByUserId(Integer userId);
    Optional<ChallengeProgress> findByChallengeIdAndUserId(Integer challengeId, Integer userId);
    List<ChallengeProgress> findByStatus(String status);
} 