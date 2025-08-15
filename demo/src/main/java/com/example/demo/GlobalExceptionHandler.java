package com.example.demo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public Map<String, Object> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", "认证失败：" + ex.getMessage());
        return result;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, Object> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("msg", "权限不足：" + ex.getMessage());
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleOtherException(Exception ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", "系统异常：" + ex.getMessage());
        return result;
    }
}