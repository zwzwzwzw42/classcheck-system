package com.classcheck.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学情统计快照（每个统计周期、每门课程一条）
 */
@Data
@TableName("statistics")
public class Statistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 统计时间（快照时刻） */
    private LocalDateTime statTime;

    private Long courseId;

    private String courseName;

    /** 提交表情的学生总人数（按学生去重，取最新反馈） */
    private Integer totalCount;

    /** 班级平均分 */
    private BigDecimal avgScore;

    /** 分数方差（总体方差） */
    private BigDecimal variance;

    private Integer score1Count;
    private Integer score2Count;
    private Integer score3Count;
    private Integer score4Count;
    private Integer score5Count;
    private Integer score6Count;

    private LocalDateTime createTime;
}
