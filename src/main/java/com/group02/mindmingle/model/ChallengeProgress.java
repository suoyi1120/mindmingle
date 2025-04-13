package com.group02.mindmingle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"challenge_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @Column(name = "challenge_id", nullable = false)
    private Integer challengeId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "current_day", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer currentDay;

    @Column(name = "completed_days", columnDefinition = "INTEGER[]")
    private Integer[] completedDays;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        if (currentDay == null) {
            currentDay = 1;
        }
        if (completedDays == null) {
            completedDays = new Integer[]{};
        }
        if (status == null) {
            status = "active";
        }
    }
} 