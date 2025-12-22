package com.example.demo.sso;

import com.example.demo.JwtUtil;
import com.example.demo.RedisUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

/**
 * 基于 JWT 的无状态 SSO 示例（/sso2/jwt/**）。
 * - /sso2/jwt/login：用户名密码校验（示例简单校验），颁发JWT
 * - /sso2/jwt/userinfo：解析请求头的 Authorization: Bearer {token}，返回用户信息
 * - 无状态：服务端不保存会话（可选地将黑名单token放入Redis实现登出）
 */
@RestController
@RequestMapping("/sso2/jwt")
public class JwtSsoExample {

    // 简单内存用户表（Demo）
    private static final Map<String, String> USER_DB = new HashMap<>();
    static {
        USER_DB.put("alice", "alice123");
        USER_DB.put("bob", "bob123");
    }

    // 可选：Redis 黑名单，登出后将 token 加入黑名单
    private final RedisUtils redis;

    public JwtSsoExample(RedisUtils redis) {
        this.redis = redis;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username,
                                                     @RequestParam String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("invalid params"));
        }
        String pwd = USER_DB.get(username);
        if (pwd == null || !pwd.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("bad credentials"));
        }
        // 签发 JWT（复用项目现有 JwtUtil；如无可按需替换）
        String token = JwtUtil.generateToken(username, Duration.ofHours(2));
        Map<String, Object> ok = new HashMap<>();
        ok.put("access_token", token);
        ok.put("token_type", "Bearer");
        ok.put("expires_in", 7200);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(name = "Authorization", required = false) String auth) {
        String token = parseBearer(auth);
        if (!StringUtils.hasText(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("no token"));
        }
        // 将 token 加入黑名单（如果 Redis 可用）
        try {
            redis.setEx("sso2:jwt:blacklist:" + token, "1", 7200);
        } catch (Exception ignore) {
            // 如果 Redis 不可用，忽略，客户端自行丢弃 token 即可（Demo）
        }
        return ResponseEntity.ok(ok("logout"));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@RequestHeader(name = "Authorization", required = false) String auth) {
        String token = parseBearer(auth);
        if (!StringUtils.hasText(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("no token"));
        }
        // 黑名单校验
        try {
            String v = redis.get("sso2:jwt:blacklist:" + token);
            if (StringUtils.hasText(v)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("token revoked"));
            }
        } catch (Exception ignore) {}

        try {
            String username = JwtUtil.parseUsername(token);
            Map<String, Object> ret = new HashMap<>();
            ret.put("username", username);
            ret.put("roles", Arrays.asList("USER"));
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid token"));
        }
    }

    private String parseBearer(String auth) {
        if (!StringUtils.hasText(auth)) return null;
        String prefix = "Bearer ";
        if (auth.startsWith(prefix)) return auth.substring(prefix.length());
        return null;
    }

    private Map<String, Object> err(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }

    private Map<String, Object> ok(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("msg", msg);
        return m;
    }
}