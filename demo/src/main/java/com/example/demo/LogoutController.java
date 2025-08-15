package com.example.demo;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class LogoutController {

    @Autowired
    private JwtCacheService jwtCacheService;

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = JwtUtil.parseToken(token);
                Date expiration = claims.getExpiration();
                long expireMillis = expiration.getTime() - System.currentTimeMillis();
                if (expireMillis > 0) {
                    jwtCacheService.blacklistToken(token, expireMillis);
                }
                return "{\"msg\": \"退出成功\"}";
            } catch (Exception e) {
                return "{\"msg\": \"token无效或已过期\"}";
            }
        }
        return "{\"msg\": \"未携带token\"}";
    }
}