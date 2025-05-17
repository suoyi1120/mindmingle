package com.group02.mindmingle.mapper;

import com.group02.mindmingle.dto.PostSegmentDTO;
import com.group02.mindmingle.model.PostSegment;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostSegmentMapper {

    public PostSegmentDTO toDTO(PostSegment segment) {
        if (segment == null) {
            return null;
        }

        PostSegmentDTO dto = new PostSegmentDTO();
        dto.setId(segment.getId());
        dto.setType(segment.getType());
        dto.setTextContent(segment.getTextContent());
        dto.setImageUrl(segment.getImageUrl());
        dto.setOrderIndex(segment.getOrderIndex());

        return dto;
    }

    public List<PostSegmentDTO> toDTOList(List<PostSegment> segments) {
        if (segments == null) {
            return List.of();
        }

        return segments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PostSegment toEntity(PostSegmentDTO dto) {
        if (dto == null) {
            return null;
        }

        PostSegment segment = new PostSegment();
        segment.setId(dto.getId());
        segment.setType(dto.getType());
        segment.setTextContent(dto.getTextContent());
        segment.setImageUrl(dto.getImageUrl());
        segment.setOrderIndex(dto.getOrderIndex());

        return segment;
    }
}