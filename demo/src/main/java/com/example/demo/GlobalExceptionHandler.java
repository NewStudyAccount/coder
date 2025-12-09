package com.example.demo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildResponse(int code, String msg, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    @ExceptionHandler(AuthenticationException.class)
    public Map<String, Object> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse(401, "认证失败：" + ex.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, Object> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(403, "权限不足：" + ex.getMessage(), null);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Map<String, Object> handleValidationException(Exception ex) {
        String msg = "参数校验失败";
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            msg = e.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ":" + err.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(msg);
        } else if (ex instanceof BindException) {
            BindException e = (BindException) ex;
            msg = e.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ":" + err.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse(msg);
        }
        return buildResponse(400, msg, null);
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleOtherException(Exception ex) {
        // 打印异常堆栈，防止泄漏详细信息
        ex.printStackTrace();
        return buildResponse(500, "系统异常", null);
    }
}
