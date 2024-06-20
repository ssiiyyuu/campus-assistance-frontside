package com.siyu.campus_assistance_frontend.entity;

import java.util.List;

import lombok.Data;

@Data
public class PaginationQuery<T> {

    private Integer pageNum;

    private Integer pageSize;

    private T condition;

    private List<Sort> sorts;

}
