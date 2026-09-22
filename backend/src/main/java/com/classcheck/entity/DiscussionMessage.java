package com.classcheck.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讨论消息（文字 + 图片，学生与教师互通）
 */
@Data
@TableName("discussion_message")
public class DiscussionMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long senderId;

    /** 文字内容（可空，与图片至少有一个） */
    private String content;

    /** 图片地址（可空） */
    private String imageUrl;

    private LocalDateTime createTime;
}
