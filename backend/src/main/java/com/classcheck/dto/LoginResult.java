package com.classcheck.dto;

import com.classcheck.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/** 登录成功返回：token + 用户信息 */
@Data
@AllArgsConstructor
public class LoginResult {
    private String token;
    private SysUser user;
}
