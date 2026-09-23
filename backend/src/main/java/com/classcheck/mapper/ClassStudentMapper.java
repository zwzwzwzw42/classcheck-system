package com.classcheck.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 班级学生关联清理（任务9 拓展模块会扩展完整的班级名单能力）。
 */
@Mapper
public interface ClassStudentMapper {

    /** 删除某学生的全部班级关联，避免删除用户时外键约束失败 */
    @Delete("DELETE FROM class_student WHERE student_id = #{studentId}")
    int deleteByStudent(@Param("studentId") Long studentId);
}
