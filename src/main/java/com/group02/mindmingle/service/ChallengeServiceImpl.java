package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.dto.challenge.CreateChallengeRequest;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeDay;
import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.model.Game;
import com.group02.mindmingle.repository.ChallengeParticipationRepository;
import com.group02.mindmingle.repository.ChallengeRepository;
import com.group02.mindmingle.repository.UserRepository;
import com.group02.mindmingle.repository.GameRepository;
import com.group02.mindmingle.repository.ChallengeDayRepository;
import com.group02.mindmingle.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ChallengeDayRepository challengeDayRepository;

    // 用户相关方法
    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

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

    // 管理员相关方法
    @Override
    @Transactional
    public ChallengeDto createChallenge(CreateChallengeRequest request) {
        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus() != null ? request.getStatus() : Challenge.ChallengeStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .challengeDays(new ArrayList<>())
                .build();

        // 如果是发布状态，检查游戏数量是否等于持续天数
        if (request.getStatus() == Challenge.ChallengeStatus.PUBLISHED) {
            if (request.getChallengeDays() == null || request.getChallengeDays().size() != request.getDuration()) {
                throw new IllegalArgumentException("Published challenge must have games for all days");
            }
        }

        Challenge savedChallenge = challengeRepository.save(challenge);

        // 保存挑战日 - 只保存实际绑定的游戏
        if (request.getChallengeDays() != null && !request.getChallengeDays().isEmpty()) {
            List<ChallengeDay> challengeDays = request.getChallengeDays().stream()
                    .map(dayRequest -> {
                        Game game = gameRepository.findById(dayRequest.getGameId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Game not found with id: " + dayRequest.getGameId()));

                        return ChallengeDay.builder()
                                .challenge(savedChallenge)
                                .game(game)
                                .dayNumber(dayRequest.getDayNumber())
                                .build();
                    })
                    .collect(Collectors.toList());

            challengeDayRepository.saveAll(challengeDays);
            savedChallenge.setChallengeDays(challengeDays);
        }

        return mapToChallengeDto(savedChallenge);
    }

    @Override
    @Transactional
    public ChallengeDto updateChallenge(Long challengeId, CreateChallengeRequest request) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        // 只允许更新草稿状态的挑战
        if (request.getStatus() == Challenge.ChallengeStatus.PUBLISHED &&
                challenge.getStatus() != Challenge.ChallengeStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT challenges can be published");
        }

        // 如果要发布，检查游戏数量是否等于持续天数
        if (request.getStatus() == Challenge.ChallengeStatus.PUBLISHED) {
            if (request.getChallengeDays() == null || request.getChallengeDays().size() != request.getDuration()) {
                throw new IllegalArgumentException("Published challenge must have games for all days");
            }
        }

        try {
            // 更新基本信息
            challenge.setTitle(request.getTitle());
            challenge.setDescription(request.getDescription());
            challenge.setStartTime(request.getStartTime());
            challenge.setEndTime(request.getEndTime());
            challenge.setStatus(request.getStatus());

            // 清空挑战日列表但先不删除数据库中的记录
            List<ChallengeDay> oldChallengeDays = new ArrayList<>(challenge.getChallengeDays());
            challenge.getChallengeDays().clear();

            // 保存挑战基本信息更新
            challengeRepository.save(challenge);

            // 删除旧的挑战日
            if (!oldChallengeDays.isEmpty()) {
                challengeDayRepository.deleteAll(oldChallengeDays);
            }

            // 处理新的挑战日
            if (request.getChallengeDays() != null && !request.getChallengeDays().isEmpty()) {
                List<ChallengeDay> newChallengeDays = new ArrayList<>();

                for (CreateChallengeRequest.CreateChallengeDayRequest dayRequest : request.getChallengeDays()) {
                    // 跳过无效的游戏ID
                    if (dayRequest.getGameId() == null || dayRequest.getGameId() <= 0) {
                        continue;
                    }

                    Game game = gameRepository.findById(dayRequest.getGameId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Game not found with id: " + dayRequest.getGameId()));

                    ChallengeDay newDay = ChallengeDay.builder()
                            .challenge(challenge)
                            .game(game)
                            .dayNumber(dayRequest.getDayNumber())
                            .build();

                    newChallengeDays.add(newDay);
                }

                // 保存新的挑战日
                if (!newChallengeDays.isEmpty()) {
                    List<ChallengeDay> savedDays = challengeDayRepository.saveAll(newChallengeDays);
                    challenge.getChallengeDays().addAll(savedDays);
                }
            }

            // 最终保存并返回
            Challenge finalChallenge = challengeRepository.save(challenge);
            return mapToChallengeDto(finalChallenge);
        } catch (Exception e) {
            // 记录详细错误信息
            e.printStackTrace();
            throw new RuntimeException("Failed to update challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public ChallengeDto getChallengeById(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));
        return mapToChallengeDto(challenge);
    }

    @Override
    public List<ChallengeDto> getAllChallengesForAdmin() {
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.stream()
                .map(this::mapToChallengeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeDto> getChallengesByStatus(Challenge.ChallengeStatus status) {
        List<Challenge> challenges = challengeRepository.findByStatus(status);
        return challenges.stream()
                .map(this::mapToChallengeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        // 只允许删除草稿状态的挑战
        if (challenge.getStatus() != Challenge.ChallengeStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT challenges can be deleted");
        }

        challengeRepository.delete(challenge);
    }

    // 定时任务：每小时检查一次挑战状态
    @Override
    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    @Transactional
    public void updateChallengeStatuses() {
        LocalDate today = LocalDate.now();

        // 发布 -> 进行中：已发布且开始时间已到或已过
        List<Challenge> publishedChallenges = challengeRepository.findByStatusAndStartTimeLessThanEqual(
                Challenge.ChallengeStatus.PUBLISHED, today);

        for (Challenge challenge : publishedChallenges) {
            challenge.setStatus(Challenge.ChallengeStatus.ACTIVE);
            challengeRepository.save(challenge);
        }

        // 进行中 -> 已结束：进行中且结束时间已过
        List<Challenge> activeChallenges = challengeRepository.findByStatusAndEndTimeLessThan(
                Challenge.ChallengeStatus.ACTIVE, today);

        for (Challenge challenge : activeChallenges) {
            challenge.setStatus(Challenge.ChallengeStatus.COMPLETED);
            challengeRepository.save(challenge);
        }
    }

    // DTO转换方法
    private ChallengeDto mapToChallengeDto(Challenge challenge) {
        List<ChallengeDayDto> challengeDayDtos = challenge.getChallengeDays().stream()
                .map(this::mapToChallengeDayDto)
                .collect(Collectors.toList());

        return ChallengeDto.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .duration(challenge.getDuration())
                .challengeStatus(challenge.getStatus())
                .startTime(challenge.getStartTime())
                .endTime(challenge.getEndTime())
                .createdAt(challenge.getCreatedAt())
                .challengeDays(challengeDayDtos)
                .build();
    }

    private ChallengeDayDto mapToChallengeDayDto(ChallengeDay challengeDay) {
        return ChallengeDayDto.builder()
                .id(challengeDay.getId())
                .challengeId(challengeDay.getChallenge().getId())
                .gameId(challengeDay.getGame().getId())
                .gameTitle(challengeDay.getGame().getTitle())
                .dayNumber(challengeDay.getDayNumber())
                .build();
    }
}
