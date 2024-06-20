package com.siyu.campus_assistance_frontend.entity.vo;

import com.siyu.campus_assistance_frontend.entity.dto.AttachmentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.CommentDTO;
import com.siyu.campus_assistance_frontend.entity.dto.SysUserBaseDTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface HolidayVO {

    @Data
    class Create {
        private String type;

        private String assigneeId;

        private String reason;

        private String startTime;

        private String endTime;

        private List<AttachmentDTO> attachments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Destroy {

        private String destroyTime;
    }

    @Data
    class Created {
        private String processId;

        private String taskId;

        private String status;

        private String type;

        private LocalDateTime createTime;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private LocalDateTime destroyTime;
    }
    @Data
    class Assigned {
        private String processId;

        private String taskId;

        private String status;

        private String type;

        private String initiator;

        private Boolean isAssigned;

        private LocalDateTime creatTime;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private LocalDateTime destroyTime;
    }

    @Data
    class Detail {

        private String status;

        private Create create;

        private Destroy destroy;

        private SysUserBaseDTO initiator;

        private List<CommentDTO> comments;

        private LocalDateTime createTime;

    }

    @Data
    class Condition {

    }


}
