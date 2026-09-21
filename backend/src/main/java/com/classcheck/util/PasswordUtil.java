package com.classcheck.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 密码工具：MD5 + 固定盐（课程作业场景）
 */
public class PasswordUtil {

    private static final String SALT = "classcheck";

    public static String encrypt(String rawPassword) {
        return DigestUtils.md5DigestAsHex((SALT + rawPassword).getBytes(StandardCharsets.UTF_8));
    }

    public static boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}
