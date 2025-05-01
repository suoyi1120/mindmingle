package com.group02.mindmingle.service.impl;

import com.group02.mindmingle.dto.challenge.ChallengeProgressDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.dto.challenge.UserChallengeProgressDTO;
import com.group02.mindmingle.dto.game.GameProgressDto;
import com.group02.mindmingle.exception.ResourceNotFoundException;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeDay;
import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.ChallengeParticipationRepository;
import com.group02.mindmingle.repository.ChallengeRepository;
import com.group02.mindmingle.repository.UserRepository;
import com.group02.mindmingle.service.IUserChallengeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserChallengeServiceImpl implements IUserChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeQueryServiceImpl challengeQueryService;

    @Override
    public void joinChallenge(Long challengeId, Long userId) {
        Optional<ChallengeParticipation> existingParticipation = participationRepository
                .findByUser_IdAndChallenge_Id(userId, challengeId);

        if (existingParticipation.isPresent()) {
            return;
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChallengeParticipation participation = new ChallengeParticipation();
        participation.setChallenge(challenge);
        participation.setUser(user);
        participationRepository.save(participation);
    }

    @Override
    public List<ChallengeParticipation> getUserChallengeHistory(Long userId) {
        return participationRepository.findByUser_Id(userId);
    }

    @Override
    public ChallengeProgressDto getUserChallengeProgress(Long challengeId, Long userId) {
        // 查找用户的挑战参与记录
        Optional<ChallengeParticipation> participationOpt = participationRepository
                .findByUser_IdAndChallenge_Id(userId, challengeId);

        if (participationOpt.isEmpty()) {
            // 如果用户没有参与记录，自动为用户创建一个参与记录（开始挑战）
            return startChallenge(challengeId, userId);
        }

        return ChallengeProgressDto.fromChallengeParticipation(participationOpt.get());
    }

    @Override
    @Transactional
    public ChallengeProgressDto startChallenge(Long challengeId, Long userId) {
        // 检查挑战是否存在
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 检查用户是否已经参与了这个挑战
        Optional<ChallengeParticipation> existingParticipation = participationRepository
                .findByUser_IdAndChallenge_Id(userId, challengeId);

        // 如果已经参与，返回现有的参与记录
        if (existingParticipation.isPresent()) {
            return ChallengeProgressDto.fromChallengeParticipation(existingParticipation.get());
        }

        // 创建新的参与记录
        ChallengeParticipation participation = new ChallengeParticipation();
        participation.setUser(user);
        participation.setChallenge(challenge);
        participation.setStartDate(LocalDateTime.now());
        participation.setStatus(ChallengeParticipation.Status.ACTIVE);
        participation.setCurrentDay(1);
        participation.setCompletedDays(new ArrayList<Integer>());

        ChallengeParticipation savedParticipation = participationRepository.save(participation);

        return ChallengeProgressDto.fromChallengeParticipation(savedParticipation);
    }

    @Override
    @Transactional
    public boolean completeDailyGame(Long challengeId, Long userId, Integer day) {
        // 查找用户的挑战参与记录
        Optional<ChallengeParticipation> participationOpt = participationRepository
                .findByUser_IdAndChallenge_Id(userId, challengeId);

        if (participationOpt.isEmpty()) {
            throw new ResourceNotFoundException("No participation found for user " + userId +
                    " in challenge " + challengeId);
        }

        ChallengeParticipation participation = participationOpt.get();

        // 检查日期是否有效
        if (day <= 0 || day > participation.getChallenge().getDuration()) {
            throw new IllegalArgumentException("Invalid day: " + day);
        }

        // 检查用户是否已经完成了指定的天数
        List<Integer> completedDays = participation.getCompletedDays();
        if (completedDays == null) {
            completedDays = new ArrayList<>();
        }

        // 如果已经完成，直接返回true
        if (completedDays.contains(day)) {
            return true;
        }

        // 添加完成的天数并保存
        completedDays.add(day);
        participation.setCompletedDays(completedDays);

        // 将状态改为JOINED
        if (participation.getStatus() == ChallengeParticipation.Status.ACTIVE) {
            participation.setStatus(ChallengeParticipation.Status.JOINED);
        }

        // 检查是否应该更新状态为已完成
        if (completedDays.size() >= participation.getChallenge().getDuration()) {
            participation.setStatus(ChallengeParticipation.Status.COMPLETED);
            participation.setEndDate(LocalDateTime.now());
        }

        participationRepository.save(participation);

        return true;
    }

    @Override
    public GameProgressDto getDailyGameWithProgress(Long challengeId, Integer day, Long userId) {
        // 获取游戏基本信息
        ChallengeDayDto challengeDayDto = challengeQueryService.getDailyGame(challengeId, day);

        // 默认未完成
        boolean completed = false;

        // 如果用户ID不为空，检查用户是否完成了该游戏
        if (userId != null) {
            Optional<ChallengeParticipation> participationOpt = participationRepository
                    .findByUser_IdAndChallenge_Id(userId, challengeId);

            if (participationOpt.isPresent()) {
                ChallengeParticipation participation = participationOpt.get();
                List<Integer> completedDays = participation.getCompletedDays();

                // 检查用户是否完成了这天的游戏
                if (completedDays != null && completedDays.contains(day)) {
                    completed = true;
                }
            }
        }

        // 转换为GameProgressDto并返回
        return GameProgressDto.fromChallengeDayDto(challengeDayDto, completed);
    }

    @Override
    public List<UserChallengeProgressDTO> getCurrentUserChallenges(Long userId) {
        // 获取用户所有正在进行中的挑战
        List<ChallengeParticipation> participations = participationRepository
                .findByUser_IdAndStatus(userId, ChallengeParticipation.Status.JOINED);

        // 将参与信息转换为DTO
        return participations.stream().map(participation -> {
            Challenge challenge = participation.getChallenge();

            // 获取当前应该完成的挑战日
            Integer currentDay = participation.getCurrentDay();

            // 获取完成的天数作为进度
            Integer progress = participation.getCompletedDays().size();

            // 获取当天的游戏标题
            String currentGameTitle = "";
            try {
                // 尝试获取当天的游戏
                if (challenge.getChallengeDays() != null && currentDay <= challenge.getChallengeDays().size()) {
                    List<ChallengeDay> days = challenge.getChallengeDays();
                    // 查找匹配当前天数的挑战日
                    for (ChallengeDay day : days) {
                        // ChallengeDay类中可能使用了不同的字段名，根据实际情况调整
                        if (day.getDayNumber() != null && day.getDayNumber().equals(currentDay)) {
                            currentGameTitle = day.getGame().getTitle();
                            break;
                        }
                    }
                } else {
                    // 如果没有在挑战天数中找到当前日期，尝试通过服务获取
                    ChallengeDayDto dayDto = challengeQueryService.getDailyGame(challenge.getId(), currentDay);
                    if (dayDto != null) {
                        currentGameTitle = dayDto.getGameTitle();
                    }
                }
            } catch (Exception e) {
                // 如果发生异常，设置一个默认值
                currentGameTitle = "今日游戏加载中...";
            }

            return UserChallengeProgressDTO.builder()
                    .id(challenge.getId())
                    .title(challenge.getTitle())
                    .description(challenge.getDescription())
                    .duration(challenge.getDuration())
                    .progress(progress)
                    .currentDay(currentDay)
                    .currentGameTitle(currentGameTitle)
                    .imageUrl(challenge.getImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }
}