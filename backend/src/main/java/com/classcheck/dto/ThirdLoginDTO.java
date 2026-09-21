package com.classcheck.dto;

import lombok.Data;

/**
 * 第三方账号登录请求（模拟微信/QQ授权）
 * platform：wechat / qq
 * phone：模拟第三方授权后获得的绑定手机号（演示用）
 */
@Data
public class ThirdLoginDTO {
    private String platform;
    private String phone;
}
