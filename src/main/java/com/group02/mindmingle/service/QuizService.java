package com.group02.mindmingle.service;
import com.group02.mindmingle.model.Quiz;
import com.group02.mindmingle.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // recognize a Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository; // injection Repository

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll(); // Get all Quiz
    }

    public List<Quiz> getQuizzesByUserId(Long userId) {
        return quizRepository.findByUserId(userId); // Get the specified user's Quiz
    }

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz); // Save Quiz
    }

    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId); // Delete Quiz
    }
}
