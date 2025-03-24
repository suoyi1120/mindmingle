package com.group02.mindmingle.service;


import com.group02.mindmingle.model.Quiz;
import com.group02.mindmingle.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


// public class QuizService {
    
// }

@Service // 让 Spring Boot 识别这是一个 Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository; // 注入 Repository

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll(); // 获取所有 Quiz
    }

    public List<Quiz> getQuizzesByUserId(Long userId) {
        return quizRepository.findByUserId(userId); // 获取指定用户的 Quiz
    }

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz); // 保存 Quiz
    }

    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId); // 删除 Quiz
    }
}
