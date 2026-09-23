package com.classcheck.dto;

import lombok.Data;

/**
 * 课程新增/修改请求
 */
@Data
public class CourseDTO {

    private Long id;

    private String name;

    private String description;
}
