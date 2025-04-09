package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.game.GameDto;
import com.group02.mindmingle.dto.game.CreateGameRequest;
import com.group02.mindmingle.exception.ResourceNotFoundException;
import com.group02.mindmingle.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {
        List<GameDto> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long id) {
        try {
            GameDto game = gameService.getGameById(id);
            return ResponseEntity.ok(game);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@Valid @RequestBody CreateGameRequest request) {
        GameDto savedGame = gameService.createGame(request);
        return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (!gameService.gameExists(id)) {
            return ResponseEntity.notFound().build();
        }
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
