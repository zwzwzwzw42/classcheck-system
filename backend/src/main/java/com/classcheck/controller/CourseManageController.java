package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.AddStudentsDTO;
import com.classcheck.dto.ClassDTO;
import com.classcheck.dto.CourseDTO;
import com.classcheck.entity.ClassInfo;
import com.classcheck.entity.Course;
import com.classcheck.entity.SysUser;
import com.classcheck.service.CourseManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师端：课程与班级管理（课程 → 班级 → 学生名单 三级）
 */
@RestController
public class CourseManageController {

    private final CourseManageService courseManageService;

    public CourseManageController(CourseManageService courseManageService) {
        this.courseManageService = courseManageService;
    }

    // ---- 课程 ----
    @GetMapping("/api/teacher/courses")
    public Result<List<Course>> myCourses() {
        return Result.success(courseManageService.listMyCourses(UserContext.getUserId()));
    }

    @PostMapping("/api/teacher/courses")
    public Result<Void> createCourse(@RequestBody CourseDTO dto) {
        courseManageService.createCourse(UserContext.getUserId(), dto);
        return Result.success("课程已创建", null);
    }

    @PutMapping("/api/teacher/courses/{id}")
    public Result<Void> updateCourse(@PathVariable Long id, @RequestBody CourseDTO dto) {
        courseManageService.updateCourse(UserContext.getUserId(), id, dto);
        return Result.success("课程已更新", null);
    }

    @DeleteMapping("/api/teacher/courses/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseManageService.deleteCourse(UserContext.getUserId(), id);
        return Result.success("课程及其班级已删除", null);
    }

    @GetMapping("/api/teacher/courses/{id}/classes")
    public Result<List<ClassInfo>> courseClasses(@PathVariable Long id) {
        return Result.success(courseManageService.listClasses(UserContext.getUserId(), id));
    }

    // ---- 班级 ----
    @PostMapping("/api/teacher/classes")
    public Result<Void> createClass(@RequestBody ClassDTO dto) {
        courseManageService.createClass(UserContext.getUserId(), dto);
        return Result.success("班级已创建", null);
    }

    @DeleteMapping("/api/teacher/classes/{id}")
    public Result<Void> deleteClass(@PathVariable Long id) {
        courseManageService.deleteClass(UserContext.getUserId(), id);
        return Result.success("班级已删除", null);
    }

    // ---- 班级学生名单 ----
    @GetMapping("/api/teacher/classes/{id}/students")
    public Result<List<SysUser>> roster(@PathVariable Long id) {
        return Result.success(courseManageService.classRoster(UserContext.getUserId(), id));
    }

    @GetMapping("/api/teacher/students/all")
    public Result<List<SysUser>> allStudents() {
        return Result.success(courseManageService.allStudents());
    }

    @PostMapping("/api/teacher/classes/{id}/students")
    public Result<Void> addStudents(@PathVariable Long id, @RequestBody AddStudentsDTO dto) {
        int added = courseManageService.addStudents(UserContext.getUserId(), id, dto.getStudentIds());
        return Result.success("已添加 " + added + " 名学生（重复的自动跳过）", null);
    }

    @DeleteMapping("/api/teacher/classes/{id}/students/{studentId}")
    public Result<Void> removeStudent(@PathVariable Long id, @PathVariable Long studentId) {
        courseManageService.removeStudent(UserContext.getUserId(), id, studentId);
        return Result.success("已从班级移除该学生", null);
    }
}
