package com.group02.mindmingle.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "quizzes") // 让 JPA 知道它对应的是数据库的 quizzes 表
@Data


public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 自增
    @Column(name = "quiz_id") // 指定数据库字段名，防止不匹配
    private Long quizId;

    @Column(name = "user_id", nullable = false) // 关联用户ID
    private Long userId;

    @Column(name = "title", nullable = false, length = 100) // Quiz 标题
    private String title;

    @Column(name = "description", columnDefinition = "TEXT") // 描述
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    
}
