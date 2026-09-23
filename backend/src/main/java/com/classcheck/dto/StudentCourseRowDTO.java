package com.classcheck.dto;

import lombok.Data;

/**
 * 学生“我的课程”查询行：课程 + 所在班级（一对多，服务层按课程聚合）
 */
@Data
public class StudentCourseRowDTO {

    private Long courseId;
    private String courseName;
    private String description;
    private String teacherName;
    private Long classId;
    private String className;
}
