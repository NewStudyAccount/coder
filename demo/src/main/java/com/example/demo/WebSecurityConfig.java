package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class WebSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .addFilter(new JwtLoginFilter(authenticationManager)) // 登录认证过滤器
            .addFilterBefore(new JwtAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class) // JWT 校验过滤器
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**").permitAll() // 公共接口无需认证
                .requestMatchers("/admin/**").hasRole("ADMIN") // 需ADMIN角色
                .anyRequest().authenticated() // 其他接口需认证
            )
            .csrf(csrf -> csrf.disable()); // 关闭 CSRF
        return http.build();
    }
}