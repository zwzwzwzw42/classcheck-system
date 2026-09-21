package com.classcheck.controller;

import com.classcheck.auth.LoginSession;
import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.*;
import com.classcheck.entity.SysUser;
import com.classcheck.mapper.SysUserMapper;
import com.classcheck.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口：密码登录、短信验证码登录、第三方（微信/QQ模拟）登录、登出、当前用户
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SysUserMapper userMapper;

    public AuthController(AuthService authService, SysUserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    /** 密码登录 */
    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody LoginDTO dto) {
        return Result.success("登录成功", authService.passwordLogin(dto.getUsername(), dto.getPassword()));
    }

    /** 发送短信验证码（模拟，验证码直接返回供演示） */
    @PostMapping("/sms/send")
    public Result<String> sendSms(@RequestBody SmsSendDTO dto) {
        String code = authService.sendSmsCode(dto.getPhone());
        return Result.success("验证码已发送（模拟短信，5分钟内有效）", code);
    }

    /** 短信验证码登录 */
    @PostMapping("/sms/login")
    public Result<LoginResult> smsLogin(@RequestBody SmsLoginDTO dto) {
        return Result.success("登录成功", authService.smsLogin(dto.getPhone(), dto.getCode()));
    }

    /** 第三方账号登录（微信/QQ模拟授权） */
    @PostMapping("/third/login")
    public Result<LoginResult> thirdLogin(@RequestBody ThirdLoginDTO dto) {
        return Result.success("授权登录成功",
                authService.thirdLogin(dto.getPlatform(), dto.getPhone()));
    }

    /** 登出 */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authService.logout(authorization.substring(7));
        }
        return Result.success("已退出登录", null);
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/me")
    public Result<SysUser> me() {
        LoginSession session = UserContext.get();
        SysUser user = userMapper.selectById(session.getUserId());
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
