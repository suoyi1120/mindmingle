package com.group02.mindmingle.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "challenge_participation")
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // participationId 已重命名为 id

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column(name = "start_date")
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")
    private LocalDateTime endDate;

    // 用户点击开始某个挑战才相当于激活，并创建对应的挑战
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    // 当前用户进行到第几天
    @Column(name = "current_day")
    private Integer current_day = 1;

    // 已完成第几天 集合
    @ElementCollection
    @CollectionTable(name = "challenge_completed_days", joinColumns = @JoinColumn(name = "participation_id"))
    @Column(name = "completed_day")
    private List<Integer> completed_day = new ArrayList<>();

    // 手动添加getter/setter方法，避免Lombok生成的方法名不符合预期

    public Integer getCurrentDay() {
        return this.current_day;
    }

    public void setCurrentDay(Integer currentDay) {
        this.current_day = currentDay;
    }

    public List<Integer> getCompletedDays() {
        return this.completed_day;
    }

    public void setCompletedDays(List<Integer> completedDays) {
        this.completed_day = completedDays;
    }

    public enum Status {
        ACTIVE,
        COMPLETED
    }
}
