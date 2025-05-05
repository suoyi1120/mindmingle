package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByChallengeId(Integer challengeId);
    Optional<Reward> findByName(String name);
    boolean existsByChallengeId(Integer challengeId);
} 