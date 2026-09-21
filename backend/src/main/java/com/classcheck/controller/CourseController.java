package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.entity.Course;
import com.classcheck.mapper.CourseMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程查询接口（登录即可访问，供下拉选择；课程的增删改在拓展模块实现）
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseMapper courseMapper;

    public CourseController(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    /** 全部课程列表 */
    @GetMapping
    public Result<List<Course>> list() {
        return Result.success(courseMapper.selectList(null));
    }
}
