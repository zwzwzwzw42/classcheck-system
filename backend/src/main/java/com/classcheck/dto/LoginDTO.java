package com.classcheck.dto;

import lombok.Data;

/** 密码登录请求 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}
