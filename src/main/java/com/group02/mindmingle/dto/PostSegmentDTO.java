package com.group02.mindmingle.dto;

import com.group02.mindmingle.model.PostSegment.SegmentType;
import lombok.Data;

@Data
public class PostSegmentDTO {
    private Long id;
    private SegmentType type;
    private String textContent;
    private String imageUrl;
    private Integer orderIndex;

    // 用于标识前端新上传图片的标记
    private String tempImageId;
}