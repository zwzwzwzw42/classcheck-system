package com.classcheck.config;

import com.classcheck.auth.LoginSession;
import com.classcheck.auth.TokenStore;
import com.classcheck.common.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录与角色权限拦截器
 * - 未登录访问业务接口返回 401
 * - 角色越权访问返回 403
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final TokenStore tokenStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginInterceptor(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return writeError(response, 401, "未登录或登录已失效");
        }
        String token = authorization.substring(7);
        LoginSession session = tokenStore.get(token);
        if (session == null) {
            return writeError(response, 401, "未登录或登录已失效");
        }
        UserContext.set(session);

        // 基于 URL 前缀的角色权限控制
        String uri = request.getRequestURI();
        Integer role = session.getRole();
        if (uri.startsWith("/api/student/") && role != 1) {
            return writeError(response, 403, "无权访问：该功能仅学生可用");
        }
        if (uri.startsWith("/api/teacher/") && role != 2) {
            return writeError(response, 403, "无权访问：该功能仅教师可用");
        }
        if (uri.startsWith("/api/admin/") && role != 3) {
            return writeError(response, 403, "无权访问：该功能仅管理员可用");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200); // 业务状态码放在 JSON body 中，前端统一处理
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
        return false;
    }
}
