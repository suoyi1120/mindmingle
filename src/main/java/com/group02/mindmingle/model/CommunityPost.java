package com.group02.mindmingle.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "community_posts")

public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isPinned;

    private boolean isVisible;

    private int likes = 0;

    // 添加与PostSegment的一对多关系
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<PostSegment> segments = new ArrayList<>();

    // 添加帮助方法以管理双向关系
    public void addSegment(PostSegment segment) {
        segments.add(segment);
        segment.setPost(this);
    }

    public void removeSegment(PostSegment segment) {
        segments.remove(segment);
        segment.setPost(null);
    }
}