package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.ChallengeDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeDayRepository extends JpaRepository<ChallengeDay, Long> {
    List<ChallengeDay> findByChallenge_Id(Long challengeId);
}