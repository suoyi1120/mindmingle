package com.group02.mindmingle.scheduler;

import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.repository.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 挑战状态定时更新组件
 */
@Component
public class ChallengeStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeStatusScheduler.class);

    @Autowired
    private ChallengeRepository challengeRepository;

    /**
     * 定时任务：每小时检查一次挑战状态
     */
    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    @Transactional
    public void updateChallengeStatuses() {
        logger.info("开始执行挑战状态更新任务");
        LocalDate today = LocalDate.now();

        // 发布 -> 进行中：已发布且开始时间已到或已过
        List<Challenge> publishedChallenges = challengeRepository.findByStatusAndStartTimeLessThanEqual(
                Challenge.ChallengeStatus.PUBLISHED, today);

        for (Challenge challenge : publishedChallenges) {
            logger.info("挑战状态从PUBLISHED更新为ACTIVE: {}", challenge.getId());
            challenge.setStatus(Challenge.ChallengeStatus.ACTIVE);
            challengeRepository.save(challenge);
        }

        // 进行中 -> 已结束：进行中且结束时间已过
        List<Challenge> activeChallenges = challengeRepository.findByStatusAndEndTimeLessThan(
                Challenge.ChallengeStatus.ACTIVE, today);

        for (Challenge challenge : activeChallenges) {
            logger.info("挑战状态从ACTIVE更新为COMPLETED: {}", challenge.getId());
            challenge.setStatus(Challenge.ChallengeStatus.COMPLETED);
            challengeRepository.save(challenge);
        }

        logger.info("挑战状态更新任务完成");
    }
}