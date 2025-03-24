package com.group02.mindmingle.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "quizzes") // Let JPA know that it corresponds to the quizzes table of the database
@Data

public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID Auto-increment
    @Column(name = "quiz_id") // Unique ID
    private Long quizId;

    @Column(name = "user_id", nullable = false) // Associated User ID
    private Long userId;

    @Column(name = "title", nullable = false, length = 100) // Quiz Titel
    private String title;

    @Column(name = "description", columnDefinition = "TEXT") // Description
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    
}
