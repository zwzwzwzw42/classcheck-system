package com.classcheck.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录会话信息（登录成功后保存在 TokenStore 中）
 */
@Data
@AllArgsConstructor
public class LoginSession {

    private Long userId;
    private String username;
    private String name;
    /** 角色：1学生 2教师 3管理员 */
    private Integer role;
}
