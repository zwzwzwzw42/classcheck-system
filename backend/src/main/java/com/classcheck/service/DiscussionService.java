package com.classcheck.service;

import com.classcheck.auth.LoginSession;
import com.classcheck.common.BusinessException;
import com.classcheck.dto.DiscussionMessageVO;
import com.classcheck.dto.PostMessageDTO;
import com.classcheck.entity.DiscussionMessage;
import com.classcheck.mapper.DiscussionMessageMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 在线讨论服务：消息拉取与发送（学生、教师共用）
 */
@Service
public class DiscussionService {

    private static final int MAX_LIMIT = 100;
    private static final int MAX_CONTENT_LEN = 1000;

    private final DiscussionMessageMapper messageMapper;

    public DiscussionService(DiscussionMessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    /**
     * 拉取最近消息，返回正序（旧→新）便于直接展示。
     */
    public List<DiscussionMessageVO> listRecent(Integer limit, Integer offset) {
        int lim = (limit == null || limit <= 0) ? 50 : Math.min(limit, MAX_LIMIT);
        int off = (offset == null || offset < 0) ? 0 : offset;
        List<DiscussionMessageVO> desc = messageMapper.selectRecent(lim, off);
        Collections.reverse(desc);
        return desc;
    }

    /** 发送消息：文字与图片至少有一个 */
    public void post(LoginSession session, PostMessageDTO dto) {
        String content = dto.getContent() == null ? null : dto.getContent().trim();
        String imageUrl = dto.getImageUrl() == null ? null : dto.getImageUrl().trim();
        boolean hasText = content != null && !content.isEmpty();
        boolean hasImage = imageUrl != null && !imageUrl.isEmpty();
        if (!hasText && !hasImage) {
            throw new BusinessException("消息内容和图片不能同时为空");
        }
        if (hasText && content.length() > MAX_CONTENT_LEN) {
            throw new BusinessException("文字内容不能超过 " + MAX_CONTENT_LEN + " 字");
        }
        DiscussionMessage msg = new DiscussionMessage();
        msg.setSenderId(session.getUserId());
        msg.setContent(hasText ? content : null);
        msg.setImageUrl(hasImage ? imageUrl : null);
        messageMapper.insert(msg);
    }
}
