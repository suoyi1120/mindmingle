package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

// public class QuizRepository {
    
// }

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByUserId(Long userId); // 根据用户ID查找 Quiz
}