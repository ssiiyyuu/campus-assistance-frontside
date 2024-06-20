package com.siyu.campus_assistance_frontend.entity.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public interface InformationVO {

    @Data
    class In {
        private String categoryId;

        private String title;

        private String cover;

        private String content;
    }

    @Data
    class Out {
        private String id;

        private String categoryId;

        private String category;

        private String title;

        private String cover;

        private String authorId;

        private String author;

        private String content;

        private Integer visits;

        private String status;

        private String departmentCode;

        private String department;

        private LocalDateTime publishTime;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;
    }

    @Data
    class Table {
        private String id;

        private String categoryId;

        private String category;

        private String title;

        private String cover;

        private String authorId;

        private String author;

        private Integer visits;

        private String status;

        private String departmentCode;

        private String department;

        private LocalDateTime publishTime;
    }

    @Data
    class Condition {
        private String categoryId;

        private String title;

        private String content;

        private String status;

        private String departmentCode;

        private List<LocalDateTime> publishTime;
    }
}
