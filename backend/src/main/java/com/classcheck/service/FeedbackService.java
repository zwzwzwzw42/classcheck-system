package com.classcheck.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classcheck.common.BusinessException;
import com.classcheck.dto.FeedbackDTO;
import com.classcheck.entity.Feedback;
import com.classcheck.mapper.FeedbackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学情反馈服务：学生提交6档表情反馈
 */
@Service
public class FeedbackService {

    private final FeedbackMapper feedbackMapper;

    public FeedbackService(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    /** 提交反馈（同一学生可重复提交，统计时以最后一次为准） */
    public void submit(Long studentId, FeedbackDTO dto) {
        if (dto.getScore() == null || dto.getScore() < 1 || dto.getScore() > 6) {
            throw new BusinessException("分值必须在 1-6 之间");
        }
        Feedback feedback = new Feedback();
        feedback.setStudentId(studentId);
        feedback.setScore(dto.getScore());
        feedback.setCourseId(dto.getCourseId());
        feedbackMapper.insert(feedback);
    }

    /** 查询某学生的反馈历史（倒序，同秒时以id倒序兜底） */
    public List<Feedback> listMine(Long studentId) {
        return feedbackMapper.selectList(new LambdaQueryWrapper<Feedback>()
                .eq(Feedback::getStudentId, studentId)
                .orderByDesc(Feedback::getCreateTime)
                .orderByDesc(Feedback::getId)
                .last("LIMIT 50"));
    }
}
