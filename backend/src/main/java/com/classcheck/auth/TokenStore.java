package com.classcheck.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 存储（内存实现，课程作业场景足够）
 */
@Component
public class TokenStore {

    private final Map<String, LoginSession> tokens = new ConcurrentHashMap<>();

    /** 登录成功后签发 token */
    public String create(LoginSession session) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, session);
        return token;
    }

    public LoginSession get(String token) {
        return token == null ? null : tokens.get(token);
    }

    public void remove(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }
}
