package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.dto.LoginRequest;
import com.admin.dto.LoginResponse;
import com.admin.dto.RefreshTokenRequest;
import com.admin.entity.LoginLog;
import com.admin.entity.Menu;
import com.admin.mapper.MenuMapper;
import com.admin.security.JwtTokenProvider;
import com.admin.security.LoginUser;
import com.admin.service.LoginLogService;
import com.admin.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MenuService menuService;
    private final LoginLogService loginLogService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private static final String LOGIN_TOKEN_KEY = "login:token:";

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            saveLoginLog(request.getUsername(), 0, "用户名或密码错误", httpRequest);
            return Result.error(401, "用户名或密码错误");
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(loginUser.getUserId(), loginUser.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(loginUser.getUserId(), loginUser.getUsername());

        loginUser.setToken(accessToken);
        loginUser.setExpireTime(System.currentTimeMillis() + accessTokenExpiration);
        redisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + loginUser.getUserId(), loginUser, 30, TimeUnit.MINUTES);

        saveLoginLog(request.getUsername(), 1, "登录成功", httpRequest);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return Result.success(new LoginResponse(accessToken, refreshToken, accessTokenExpiration / 1000));
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            return Result.error(401, "refreshToken无效或已过期");
        }
        if (!jwtTokenProvider.isRefreshToken(request.getRefreshToken())) {
            return Result.error(401, "不是refreshToken");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());

        String accessToken = jwtTokenProvider.createAccessToken(userId, username);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, username);

        return Result.success(new LoginResponse(accessToken, newRefreshToken, accessTokenExpiration / 1000));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser != null) {
            redisTemplate.delete(LOGIN_TOKEN_KEY + loginUser.getUserId());
        }
        SecurityContextHolder.clearContext();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Menu> menus = menuService.selectMenuTree(loginUser.getUserId());

        Map<String, Object> info = new HashMap<>();
        info.put("user", loginUser);
        info.put("menus", menus);
        info.put("permissions", loginUser.getPermissions());
        return Result.success(info);
    }

    private void saveLoginLog(String username, int status, String msg, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setIpaddr(request.getRemoteAddr());
        log.setStatus(status);
        log.setMsg(msg);
        log.setLoginTime(LocalDateTime.now());
        try {
            loginLogService.createLoginLog(log);
        } catch (Exception ignored) {}
    }
}
