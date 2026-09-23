package com.classcheck.dto;

import lombok.Data;

import java.util.List;

/**
 * 学生“我的课程”视图：一门课程 + 该课程下我所在的班级
 */
@Data
public class StudentCourseVO {

    private Long courseId;
    private String courseName;
    private String description;
    private String teacherName;
    private List<ClassSimpleVO> classes;
}
