package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.DiscussionMessageVO;
import com.classcheck.dto.PostMessageDTO;
import com.classcheck.service.DiscussionService;
import com.classcheck.service.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线讨论接口（学生/教师登录后均可使用）
 */
@RestController
@RequestMapping("/api/discuss")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final FileStorageService fileStorageService;

    public DiscussionController(DiscussionService discussionService,
                                FileStorageService fileStorageService) {
        this.discussionService = discussionService;
        this.fileStorageService = fileStorageService;
    }

    /** 分页拉取最近消息（正序返回） */
    @GetMapping("/messages")
    public Result<List<DiscussionMessageVO>> messages(
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        return Result.success(discussionService.listRecent(limit, offset));
    }

    /** 发送文字/图片消息 */
    @PostMapping("/messages")
    public Result<Void> post(@RequestBody PostMessageDTO dto) {
        discussionService.post(UserContext.get(), dto);
        return Result.success("发送成功", null);
    }

    /** 上传讨论区图片，返回可访问地址 */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success("上传成功", data);
    }
}
