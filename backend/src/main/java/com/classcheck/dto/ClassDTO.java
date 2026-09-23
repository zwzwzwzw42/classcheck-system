package com.classcheck.dto;

import lombok.Data;

/**
 * 班级新增请求
 */
@Data
public class ClassDTO {

    private Long id;

    private String name;

    private Long courseId;
}
