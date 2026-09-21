package com.classcheck.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classcheck.auth.LoginSession;
import com.classcheck.auth.SmsCodeStore;
import com.classcheck.auth.TokenStore;
import com.classcheck.common.BusinessException;
import com.classcheck.dto.LoginResult;
import com.classcheck.entity.SysUser;
import com.classcheck.mapper.SysUserMapper;
import com.classcheck.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * 认证服务：密码登录、短信验证码登录、第三方（微信/QQ模拟）登录
 */
@Service
public class AuthService {

    private final SysUserMapper userMapper;
    private final TokenStore tokenStore;
    private final SmsCodeStore smsCodeStore;

    public AuthService(SysUserMapper userMapper, TokenStore tokenStore, SmsCodeStore smsCodeStore) {
        this.userMapper = userMapper;
        this.tokenStore = tokenStore;
        this.smsCodeStore = smsCodeStore;
    }

    /** 密码登录 */
    public LoginResult passwordLogin(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("用户名和密码不能为空");
        }
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException("账号不存在");
        }
        checkEnabled(user);
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return issueToken(user);
    }

    /** 发送短信验证码（模拟），返回验证码供演示 */
    public String sendSmsCode(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessException("手机号不能为空");
        }
        SysUser user = findByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        checkEnabled(user);
        return smsCodeStore.send(phone);
    }

    /** 短信验证码登录 */
    public LoginResult smsLogin(String phone, String code) {
        if (phone == null || code == null || code.isBlank()) {
            throw new BusinessException("手机号和验证码不能为空");
        }
        if (!smsCodeStore.verify(phone, code)) {
            throw new BusinessException("验证码错误或已过期");
        }
        SysUser user = findByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        checkEnabled(user);
        return issueToken(user);
    }

    /** 第三方账号登录（模拟微信/QQ授权） */
    public LoginResult thirdLogin(String platform, String phone) {
        if (!"wechat".equals(platform) && !"qq".equals(platform)) {
            throw new BusinessException("不支持的第三方平台");
        }
        if (phone == null || phone.isBlank()) {
            throw new BusinessException("缺少第三方绑定手机号");
        }
        SysUser user = findByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号尚未绑定系统账号，请先联系管理员注册");
        }
        checkEnabled(user);
        return issueToken(user);
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }

    public LoginSession getSession(String token) {
        return tokenStore.get(token);
    }

    // ---------- 内部方法 ----------

    private SysUser findByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, phone));
    }

    private void checkEnabled(SysUser user) {
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
    }

    private LoginResult issueToken(SysUser user) {
        LoginSession session = new LoginSession(user.getId(), user.getUsername(), user.getName(), user.getRole());
        String token = tokenStore.create(session);
        user.setPassword(null); // 返回前清除密码
        return new LoginResult(token, user);
    }
}
