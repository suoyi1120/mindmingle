package com.group02.mindmingle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    private LocalDate startTime;

    private LocalDate endTime;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String iconUrl = "default-challenge.png";

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeDay> challengeDays = new ArrayList<>();

    public enum ChallengeStatus {
        DRAFT, PUBLISHED, ACTIVE, COMPLETED
    }
}
