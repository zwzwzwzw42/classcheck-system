package com.classcheck.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学情反馈（学生提交的6档表情，分值1-6）
 */
@Data
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    /** 分值 1-6（6完全理解 1完全没理解） */
    private Integer score;

    /** 所属课程ID（可空=默认课堂） */
    private Long courseId;

    private LocalDateTime createTime;
}
