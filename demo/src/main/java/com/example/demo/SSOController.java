package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SSO单点登录控制器
 * /sso/login 跳转到认证中心
 * /sso/callback 认证中心回调
 */
@RestController
@RequestMapping("/sso")
public class SSOController {

    @Autowired
    private JwtCacheService jwtCacheService;

    /**
     * 模拟跳转到SSO认证中心
     * 实际项目可重定向到CAS、OAuth2等认证中心
     */
    @GetMapping("/login")
    public void ssoLogin(HttpServletResponse response) throws IOException {
        // 模拟跳转到SSO认证中心，实际应为认证中心地址
        // 这里直接跳转到本地模拟认证中心页面
        response.sendRedirect("/public/sso-login.html");
    }

    /**
     * SSO认证中心回调
     * 认证中心会回传一个token参数
     * 这里校验token并生成本地JWT
     */
    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> ssoCallback(@RequestParam("token") String ssoToken) {
        Map<String, Object> result = new HashMap<>();
        // 1. 校验SSO token（实际应调用认证中心接口校验）
        boolean valid = "sso-demo-token".equals(ssoToken); // demo校验
        if (!valid) {
            result.put("code", 401);
            result.put("msg", "SSO token无效");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        // 2. 生成本地JWT
        String username = "ssoUser"; // demo，实际应从认证中心获取
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        String jwt = JwtUtil.generateToken(username, claims);
        jwtCacheService.setToken(username, jwt, JwtUtil.getExpiration());
        result.put("code", 200);
        result.put("msg", "SSO登录成功");
        result.put("jwt", jwt);
        return ResponseEntity.ok(result);
    }
}