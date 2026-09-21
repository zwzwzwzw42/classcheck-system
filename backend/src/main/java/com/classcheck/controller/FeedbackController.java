package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.FeedbackDTO;
import com.classcheck.entity.Feedback;
import com.classcheck.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学情反馈接口（学生）
 */
@RestController
@RequestMapping("/api/student/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /** 提交6档表情反馈 */
    @PostMapping
    public Result<Void> submit(@RequestBody FeedbackDTO dto) {
        feedbackService.submit(UserContext.getUserId(), dto);
        return Result.success("反馈成功", null);
    }

    /** 查看我的反馈历史 */
    @GetMapping("/mine")
    public Result<List<Feedback>> mine() {
        return Result.success(feedbackService.listMine(UserContext.getUserId()));
    }
}
