package com.siyu.campus_assistance_frontend.entity;

import lombok.Data;

@Data
public class Result<T> {
    private Boolean success;
    private String status;
    private String message;
    private T data;
}

