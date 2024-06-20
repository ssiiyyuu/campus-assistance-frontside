package com.siyu.campus_assistance_frontend.entity.vo;

import lombok.Data;


public interface SysUserVO {


    @Data
    class Out {
        private String id;

        private String username;

        private String nickname;

        private String avatar;

        private String email;

        private String userType;

        private String departmentCode;
    }

}
