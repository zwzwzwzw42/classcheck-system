package com.classcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classcheck.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

    /**
     * 查询每位学生在每门课程下的最新一条反馈（按学生+课程去重，取自增主键最大的一条）。
     * 用 MAX(id) 而非 MAX(create_time)，避免同一秒内多次提交时间戳相同导致重复计数。
     * course_id 为 NULL 时用 IFNULL 归一为 0 处理。
     */
    @Select("""
            SELECT f.* FROM feedback f
            INNER JOIN (
                SELECT student_id, IFNULL(course_id, 0) AS cid, MAX(id) AS max_id
                FROM feedback
                GROUP BY student_id, IFNULL(course_id, 0)
            ) t ON f.id = t.max_id
            """)
    List<Feedback> selectLatestPerStudent();
}
