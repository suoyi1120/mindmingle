package com.group02.mindmingle.scheduler;

import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.repository.ChallengeParticipationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户挑战进度定时更新组件
 */
@Component
public class UserProgressScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UserProgressScheduler.class);

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    /**
     * 定时任务：每天凌晨更新用户挑战进度
     */
    @Scheduled(cron = "0 0 0 * * *") // 每天凌晨12点执行
    @Transactional
    public void updateUserChallengeProgress() {
        logger.info("开始执行用户挑战进度更新任务");

        // 获取所有进行中的挑战参与记录
        List<ChallengeParticipation> activeParticipations = participationRepository
                .findByStatus(ChallengeParticipation.Status.ACTIVE);

        logger.info("找到 {} 个进行中的挑战参与记录", activeParticipations.size());

        for (ChallengeParticipation participation : activeParticipations) {
            Integer currentDay = participation.getCurrentDay();
            List<Integer> completedDays = participation.getCompletedDays();

            // 只在用户完成了当天的游戏时才更新进度
            if (completedDays != null && completedDays.contains(currentDay)) {
                // 检查是否已经完成全部挑战
                if (currentDay >= participation.getChallenge().getDuration()) {
                    // 挑战全部完成
                    participation.setStatus(ChallengeParticipation.Status.COMPLETED);
                    participation.setEndDate(LocalDateTime.now());
                    logger.info("用户 {} 完成了挑战 {}",
                            participation.getUser().getId(),
                            participation.getChallenge().getId());
                } else {
                    // 更新到下一天
                    participation.setCurrentDay(currentDay + 1);
                    logger.info("用户 {} 的挑战 {} 进度更新到第 {} 天",
                            participation.getUser().getId(),
                            participation.getChallenge().getId(),
                            currentDay + 1);
                }

                participationRepository.save(participation);
            }
        }

        logger.info("用户挑战进度更新任务完成");
    }
}