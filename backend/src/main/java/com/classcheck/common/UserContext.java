package com.classcheck.common;

import com.classcheck.auth.LoginSession;

/**
 * 当前登录用户上下文（基于 ThreadLocal，请求结束后必须清理）
 */
public class UserContext {

    private static final ThreadLocal<LoginSession> HOLDER = new ThreadLocal<>();

    public static void set(LoginSession session) {
        HOLDER.set(session);
    }

    public static LoginSession get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        LoginSession s = HOLDER.get();
        return s == null ? null : s.getUserId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
