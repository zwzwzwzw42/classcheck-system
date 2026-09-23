package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.StudentCourseVO;
import com.classcheck.service.CourseManageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生端：我的课程与班级（拓展模块）
 */
@RestController
@RequestMapping("/api/student/courses")
public class StudentCourseController {

    private final CourseManageService courseManageService;

    public StudentCourseController(CourseManageService courseManageService) {
        this.courseManageService = courseManageService;
    }

    @GetMapping
    public Result<List<StudentCourseVO>> myCourses() {
        return Result.success(courseManageService.myCourses(UserContext.getUserId()));
    }
}
