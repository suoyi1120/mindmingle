package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByStatus(Challenge.ChallengeStatus status);

    List<Challenge> findByStatusAndStartTimeLessThanEqual(Challenge.ChallengeStatus status, LocalDate date);

    List<Challenge> findByStatusAndEndTimeLessThan(Challenge.ChallengeStatus status, LocalDate date);
}
