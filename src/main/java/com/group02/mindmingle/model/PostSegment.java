package com.group02.mindmingle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_segments")
public class PostSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 片段类型：TEXT 或 IMAGE
    @Enumerated(EnumType.STRING)
    private SegmentType type;

    // 文本内容，对于TEXT类型
    @Column(columnDefinition = "TEXT")
    private String textContent;

    // 图片URL，对于IMAGE类型
    private String imageUrl;

    // 片段的排序索引
    private Integer orderIndex;

    // 所属帖子
    @ManyToOne
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    public enum SegmentType {
        TEXT,
        IMAGE
    }
}