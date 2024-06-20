package com.siyu.campus_assistance_frontend.entity.vo;

import com.siyu.campus_assistance_frontend.entity.dto.AttachmentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.CommentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.SysUserBaseDTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public interface CampusReportVO {
    @Data
    class Create {
        private String eventId;

        private String event;

        private String level;

        private String description;

        private String createLocation;

        private List<AttachmentDTO> attachments;
    }

    @Data
    class Transact {

        CommentDTO comment;

        private List<AttachmentDTO> attachments;
    }

    @Data
    class Detail {
        Create create;

        Transact transact;

        private String status;

        private SysUserBaseDTO initiator;

        private LocalDateTime createTime;
    }

    @Data
    class Created {
        private String processId;

        private String taskId;

        private String status;

        private String event;

        private String level;

        private String description;

        private LocalDateTime createTime;
    }

    @Data
    class Assigned {
        private String processId;

        private String taskId;

        private String status;

        private String event;

        private String level;

        private String description;

        private String initiator;

        private LocalDateTime createTime;

        private Boolean isAssigned;
    }
}
