package com.siyu.campus_assistance_frontend.entity;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResult <T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long pageNum;

    private Long pageSize;

    private Long total;

    private Long totalPages;

    private List<T> data;
}
