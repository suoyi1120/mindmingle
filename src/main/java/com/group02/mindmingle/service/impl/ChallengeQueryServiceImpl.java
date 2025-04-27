package com.group02.mindmingle.service.impl;

import com.group02.mindmingle.dto.challenge.ChallengeDto;
import com.group02.mindmingle.dto.challenge.ChallengeDayDto;
import com.group02.mindmingle.dto.game.GameProgressDto;
import com.group02.mindmingle.exception.ResourceNotFoundException;
import com.group02.mindmingle.mapper.ChallengeMapper;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.model.ChallengeDay;
import com.group02.mindmingle.model.ChallengeParticipation;
import com.group02.mindmingle.repository.ChallengeParticipationRepository;
import com.group02.mindmingle.repository.ChallengeRepository;
import com.group02.mindmingle.service.IChallengeQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeQueryServiceImpl implements IChallengeQueryService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private ChallengeMapper challengeMapper;

    @Override
    public List<Challenge> getAllChallengesModel() {
        return challengeRepository.findAll();
    }

    @Override
    public List<Challenge> getChallengesByStatusModel(Challenge.ChallengeStatus status) {
        return challengeRepository.findByStatus(status);
    }

    @Override
    public ChallengeDto getChallengeById(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));
        return challengeMapper.mapToChallengeDto(challenge);
    }

    @Override
    public List<ChallengeDto> getAllChallengesForAdmin() {
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.stream()
                .map(challengeMapper::mapToChallengeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeDto> getChallengesByStatus(Challenge.ChallengeStatus status) {
        List<Challenge> challenges = challengeRepository.findByStatus(status);
        return challenges.stream()
                .map(challengeMapper::mapToChallengeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeDto> getDefaultChallenges(String status) {
        if (status != null && !status.isEmpty()) {
            try {
                Challenge.ChallengeStatus challengeStatus = Challenge.ChallengeStatus.valueOf(status.toUpperCase());
                return getChallengesByStatus(challengeStatus);
            } catch (IllegalArgumentException e) {
                // 如果状态参数无效，返回ACTIVE状态的挑战
                return getChallengesByStatus(Challenge.ChallengeStatus.ACTIVE);
            }
        }
        // 默认返回ACTIVE状态的挑战
        return getChallengesByStatus(Challenge.ChallengeStatus.ACTIVE);
    }

    @Override
    public List<ChallengeDayDto> getChallengeDailyGames(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        // 从挑战中获取所有挑战日，并按天数排序
        List<ChallengeDay> challengeDays = challenge.getChallengeDays().stream()
                .sorted((day1, day2) -> day1.getDayNumber().compareTo(day2.getDayNumber()))
                .collect(Collectors.toList());

        return challengeDays.stream()
                .map(challengeMapper::mapToChallengeDayDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChallengeDayDto getDailyGame(Long challengeId, Integer day) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        // 查找对应天数的挑战日
        ChallengeDay challengeDay = challenge.getChallengeDays().stream()
                .filter(cd -> cd.getDayNumber().equals(day))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Day " + day + " not found for challenge with id: " + challengeId));

        return challengeMapper.mapToChallengeDayDto(challengeDay);
    }
}