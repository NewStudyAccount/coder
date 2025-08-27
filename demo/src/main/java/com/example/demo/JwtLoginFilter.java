package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtCacheService jwtCacheService;

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtCacheService jwtCacheService) {
        this.authenticationManager = authenticationManager;
        this.jwtCacheService = jwtCacheService;
        setFilterProcessesUrl("/login"); // 登录接口
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            Map<String, String> user = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String username = user.get("username");
            String password = user.get("password");
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = authResult.getName();
        String token = JwtUtil.generateToken(username, new HashMap<>());
        // 存储token到Redis
        jwtCacheService.setToken(username, token, JwtUtil.getExpiration());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"msg\": \"登录失败: " + failed.getMessage() + "\"}");
    }
}