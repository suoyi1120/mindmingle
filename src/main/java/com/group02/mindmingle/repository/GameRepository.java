package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // 可以添加自定义查询方法
}
