package com.classcheck.dto;

import lombok.Data;

/** 学情反馈提交请求 */
@Data
public class FeedbackDTO {
    /** 分值 1-6 */
    private Integer score;
    /** 课程ID（可空=默认课堂） */
    private Long courseId;
}
