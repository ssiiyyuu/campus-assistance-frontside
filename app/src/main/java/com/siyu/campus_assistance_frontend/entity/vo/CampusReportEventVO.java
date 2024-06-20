package com.siyu.campus_assistance_frontend.entity.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

public interface CampusReportEventVO {
    @Data
    class In {
	    private String parentId;

	    private String name;

	    private String level;

    }

    @Data
    class Out {
        private String id;

		private String parentId;

	    private String name;

	    private String level;

    }

    @Data
    class Condition {
	    private String name;

	    private String level;

    }

    @Data
    class Table {
        private String id;

		private String parentId;

	    private String name;

	    private String level;

    }

	@Data
	class Tree implements Serializable {

		private static final long serialVersionUID = 1L;

		private String id;

		private String parentId;

		private String name;

		private String level;

		private List<Tree> childList;
	}
}
