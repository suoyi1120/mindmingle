package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.ChallengeParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {

    Optional<ChallengeParticipation> findByUser_IdAndChallenge_Id(Long userId, Long challengeId);

    List<ChallengeParticipation> findByUser_Id(Long id);

    List<ChallengeParticipation> findByStatus(ChallengeParticipation.Status status);

    List<ChallengeParticipation> findByUser_IdAndStatus(Long userId, ChallengeParticipation.Status status);

}
