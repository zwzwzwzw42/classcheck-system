package com.classcheck.dto;

import lombok.Data;

/** 短信验证码登录请求 */
@Data
public class SmsLoginDTO {
    private String phone;
    private String code;
}
