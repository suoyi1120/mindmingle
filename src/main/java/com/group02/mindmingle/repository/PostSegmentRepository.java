package com.group02.mindmingle.repository;

import com.group02.mindmingle.model.PostSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostSegmentRepository extends JpaRepository<PostSegment, Long> {
    List<PostSegment> findByPostIdOrderByOrderIndexAsc(Long postId);

    void deleteByPostId(Long postId);
}