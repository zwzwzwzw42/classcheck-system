package com.classcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classcheck.dto.StudentCourseRowDTO;
import com.classcheck.entity.ClassStudent;
import com.classcheck.entity.SysUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 班级学生关联 Mapper（拓展模块：课程与班级管理）
 */
@Mapper
public interface ClassStudentMapper extends BaseMapper<ClassStudent> {

    /** 删除某学生的全部班级关联，避免删除用户时外键约束失败 */
    @Delete("DELETE FROM class_student WHERE student_id = #{studentId}")
    int deleteByStudent(@Param("studentId") Long studentId);

    /** 班级学生名单（含学生信息） */
    @Select("SELECT u.id, u.username, u.name, u.role, u.age, u.gender, u.phone, " +
            "u.student_no AS studentNo, u.grade, u.class_name AS className, u.major, u.status, u.create_time " +
            "FROM class_student cs JOIN sys_user u ON u.id = cs.student_id " +
            "WHERE cs.class_id = #{classId} ORDER BY u.student_no, u.id")
    List<SysUser> selectRoster(@Param("classId") Long classId);

    /** 学生已加入的课程与班级行（服务层按课程聚合） */
    @Select("SELECT c.id AS courseId, c.name AS courseName, c.description AS description, " +
            "t.name AS teacherName, ci.id AS classId, ci.name AS className " +
            "FROM class_student cs " +
            "JOIN class_info ci ON ci.id = cs.class_id " +
            "JOIN course c ON c.id = ci.course_id " +
            "JOIN sys_user t ON t.id = c.teacher_id " +
            "WHERE cs.student_id = #{studentId} ORDER BY c.id, ci.id")
    List<StudentCourseRowDTO> selectStudentCourseRows(@Param("studentId") Long studentId);
}
