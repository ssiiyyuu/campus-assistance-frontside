package com.siyu.campus_assistance_frontend.entity.vo;

import java.util.List;

import lombok.Data;

public interface CategoryVO {
    @Data
    class Tree {
        private String id;

        private String parentId;

        private String name;

        private String remark;

        private List<Tree> childList;
    }

    @Data
    class Table {
        private String id;

        private String parentId;

        private String parent;

        private String name;

        private String remark;
    }

    @Data
    class Dropdown {
        private String id;

        private String name;
    }
}
