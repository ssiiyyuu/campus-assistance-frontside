package com.siyu.campus_assistance_frontend.entity;

import lombok.Data;

@Data
public class Sort {

    private String fieldName;

    private String direction;

    public static boolean isAsc(Sort sort) {
        return !isDesc(sort);
    }

    public static boolean isDesc(Sort sort) {
        return "desc".equalsIgnoreCase(sort.getDirection());
    }
}