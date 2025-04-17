package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByIsVisibleTrueOrderByCreatedAtDesc();
    List<CommunityPost> findByIsVisibleTrue();
    List<CommunityPost> findByAuthorId(Long userId);
}