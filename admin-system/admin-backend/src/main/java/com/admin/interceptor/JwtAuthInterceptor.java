package com.admin.interceptor;

import com.admin.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader(header);
        if (authHeader == null || !authHeader.startsWith(prefix.trim())) {
            sendError(response, 401, "未提供认证令牌");
            return false;
        }

        String token = authHeader.substring(prefix.trim().length()).trim();
        try {
            if (jwtUtil.isTokenExpired(token)) {
                sendError(response, 401, "认证令牌已过期");
                return false;
            }

            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            request.setAttribute("currentUserId", userId);
            request.setAttribute("currentUsername", username);
            request.setAttribute("currentRole", role);
            return true;
        } catch (JwtException e) {
            sendError(response, 401, "无效的认证令牌");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
