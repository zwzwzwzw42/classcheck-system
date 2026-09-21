package com.classcheck.dto;

import lombok.Data;

/** 发送短信验证码请求 */
@Data
public class SmsSendDTO {
    private String phone;
}
