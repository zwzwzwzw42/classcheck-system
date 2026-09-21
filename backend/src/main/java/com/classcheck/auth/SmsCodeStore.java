package com.classcheck.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信验证码存储（模拟短信服务：验证码存内存，5分钟有效）
 */
@Component
public class SmsCodeStore {

    private record CodeEntry(String code, long expireAt) {
    }

    private static final long TTL_MS = 5 * 60 * 1000L;

    private final Map<String, CodeEntry> codes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /** 生成并保存验证码，返回验证码（模拟环境直接返回，由前端提示） */
    public String send(String phone) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        codes.put(phone, new CodeEntry(code, System.currentTimeMillis() + TTL_MS));
        return code;
    }

    /** 校验验证码，校验成功后失效 */
    public boolean verify(String phone, String code) {
        CodeEntry entry = codes.get(phone);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() > entry.expireAt()) {
            codes.remove(phone);
            return false;
        }
        if (entry.code().equals(code)) {
            codes.remove(phone);
            return true;
        }
        return false;
    }
}
