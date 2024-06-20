package com.siyu.campus_assistance_frontend.entity.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public interface NotificationVO {
    @Data
    class In {
		private List<String> toList;

		private String content;


    }

    @Data
    class Out {
        private String id;

		private String type;

		private String from;

		private String to;

		private String content;

		private LocalDateTime createTime;


    }

    @Data
    class Condition {
		private String type;
    }

    @Data
    class Table {
        private String id;

		private String type;

		private String from;

		private String to;

		private Boolean read;

		private LocalDateTime createTime;
    }
}
