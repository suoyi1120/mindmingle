package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    // 可以添加自定义查询方法
}