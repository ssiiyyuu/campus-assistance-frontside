package com.siyu.campus_assistance_frontend.entity.vo;

import com.siyu.campus_assistance_frontend.entity.dto.CommentDTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public interface FlowableVO {

    @Data
    class Stage {
        private String name;

        private String assignee;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private Long duration;

        private List<CommentDTO> comments;
    }

}
