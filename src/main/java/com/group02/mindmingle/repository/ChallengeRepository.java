package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
