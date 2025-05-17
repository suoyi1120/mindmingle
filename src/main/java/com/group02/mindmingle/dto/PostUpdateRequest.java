package com.group02.mindmingle.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostUpdateRequest {
    private String title;
    private boolean removeCoverImage; // 是否移除封面图片
    private List<PostSegmentDTO> segments = new ArrayList<>();
}