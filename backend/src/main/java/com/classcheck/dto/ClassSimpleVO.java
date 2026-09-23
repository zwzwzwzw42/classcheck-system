package com.classcheck.dto;

import lombok.Data;

/**
 * 班级简要信息（学生“我的课程”内嵌套）
 */
@Data
public class ClassSimpleVO {

    private Long classId;
    private String className;
}
