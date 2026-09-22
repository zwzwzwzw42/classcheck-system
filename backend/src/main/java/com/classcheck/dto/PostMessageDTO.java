package com.classcheck.dto;

import lombok.Data;

/** 发送讨论消息请求 */
@Data
public class PostMessageDTO {

    /** 文字内容（与图片至少有一个） */
    private String content;

    /** 已上传图片的访问地址 */
    private String imageUrl;
}
