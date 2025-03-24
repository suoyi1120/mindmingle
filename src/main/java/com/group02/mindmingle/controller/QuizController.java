package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.Quiz;
import com.group02.mindmingle.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes") // 统一管理 Quiz API

public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping // 获取所有 Quiz
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/user/{userId}") // 获取指定用户的 Quiz
    public List<Quiz> getUserQuizzes(@PathVariable Long userId) {
        return quizService.getQuizzesByUserId(userId);
    }

    @PostMapping // 创建 Quiz
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.createQuiz(quiz);
    }

    @DeleteMapping("/{quizId}") // 删除 Quiz
    public void deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
    }
    
}
