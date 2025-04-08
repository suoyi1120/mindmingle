package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.game.ChallengeDto;
import com.group02.mindmingle.dto.game.CreateChallengeRequest;
import com.group02.mindmingle.model.Challenge;
import com.group02.mindmingle.repository.ChallengeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/game")
public class ChallengeController {

    private final ChallengeRepository challengeRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public ChallengeController(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllChallenges() {
        List<ChallengeDto> challenges = challengeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(challenges);
    }

    @PostMapping
    public ResponseEntity<ChallengeDto> createChallenge(@Valid @RequestBody CreateChallengeRequest request) {
        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl() != null && !request.getImageUrl().isEmpty()
                        ? request.getImageUrl()
                        : "https://via.placeholder.com/300x150")
                .createdAt(LocalDateTime.now())
                .build();

        Challenge savedChallenge = challengeRepository.save(challenge);
        return new ResponseEntity<>(convertToDto(savedChallenge), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        if (!challengeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        challengeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ChallengeDto convertToDto(Challenge challenge) {
        return ChallengeDto.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .category(challenge.getCategory())
                .imageUrl(challenge.getImageUrl())
                .createdAt(challenge.getCreatedAt().format(DATE_FORMATTER))
                .build();
    }
}
