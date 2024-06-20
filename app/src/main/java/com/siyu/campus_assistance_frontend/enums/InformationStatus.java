package com.siyu.campus_assistance_frontend.enums;

import lombok.Getter;

@Getter
public enum InformationStatus {

    CREATED("草稿"),

    SCHEDULED("定时发布"),

    PUBLISHED("已发布"),

    OFFLINE("已下线");

    private final String status;

    InformationStatus(String status) {
        this.status = status;
    }

}
