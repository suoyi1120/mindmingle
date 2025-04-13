package com.group02.mindmingle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge_games", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"challenge_id", "day"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challenge_id", nullable = false)
    private Integer challengeId;

    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(nullable = false)
    private Integer day;
} 