package com.group02.mindmingle.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "challenge_participation")
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column(name = "start_date")
    private LocalDateTime startDate = LocalDateTime.now();

    private Integer progress = 0;

    private Boolean completed = false;

    // Getters and Setters
}
