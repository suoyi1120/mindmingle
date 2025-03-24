package com.group02.mindmingle.controller;
import com.group02.mindmingle.model.Quiz;
import com.group02.mindmingle.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes") // Quiz API

public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping // Get all Quiz
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/user/{userId}") // Get Quiz by UserId
    public List<Quiz> getUserQuizzes(@PathVariable Long userId) {
        return quizService.getQuizzesByUserId(userId);
    }

    @PostMapping // Create a Quiz
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.createQuiz(quiz);
    }

    @DeleteMapping("/{quizId}") // Delete a Quiz
    public void deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
    }
    
}
