package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByUser_Id(Long userId);
    Optional<Challenge> findByTitle(String title);
    List<Challenge> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Challenge> findByDuration(Integer duration);
}
