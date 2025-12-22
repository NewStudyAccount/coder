package com.example.demo.sso;

import com.example.demo.RedisUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

/**
 * 基于 Session/Ticket + Redis 的集中会话 SSO 示例（/sso2/session/**）。
 * - /sso2/session/login：用户名密码校验（示例简单校验），生成 ticket（随机字符串），写入 Redis（或内存）并返回
 * - /sso2/session/userinfo：根据请求头 Authorization: Ticket {ticket} 校验并返回用户信息
 * - /sso2/session/logout：删除 ticket，实现登出
 * - Redis 可选，如不可用则回落到内存 Map。ticket 具有过期时间（默认 2 小时）。
 */
@RestController
@RequestMapping("/sso2/session")
public class SessionSsoExample {

    // 简单内存用户表（Demo）
    private static final Map<String, String> USER_DB = new HashMap<>();
    static {
        USER_DB.put("alice", "alice123");
        USER_DB.put("bob", "bob123");
    }

    private final RedisUtils redis;
    // 回落：内存 Ticket 存储（ticket -> username, expireAt）
    private static final Map<String, SessionEntry> LOCAL_SESSION = new HashMap<>();
    private static final long DEFAULT_EXPIRE_SECONDS = 2 * 60 * 60;

    public SessionSsoExample(RedisUtils redis) {
        this.redis = redis;
    }

    /**
     * 登录，生成 ticket 并存储（Redis优先，失败则使用内存）
     */
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
        String ticket = UUID.randomUUID().toString().replace("-", "");
        long expireSec = DEFAULT_EXPIRE_SECONDS;

        // 优先写入 Redis
        boolean useRedis = false;
        try {
            redis.setEx("sso2:session:ticket:" + ticket, username, (int) expireSec);
            useRedis = true;
        } catch (Exception ignore) {
            useRedis = false;
        }
        if (!useRedis) {
            synchronized (LOCAL_SESSION) {
                LOCAL_SESSION.put(ticket, new SessionEntry(username, System.currentTimeMillis() + expireSec * 1000));
            }
        }
        Map<String, Object> ok = new HashMap<>();
        ok.put("ticket", ticket);
        ok.put("token_type", "Ticket");
        ok.put("expires_in", expireSec);
        return ResponseEntity.ok(ok);
    }

    /**
     * 登出，删除 ticket
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(name = "Authorization", required = false) String auth) {
        String ticket = parseTicket(auth);
        if (!StringUtils.hasText(ticket)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("no ticket"));
        }
        boolean removed = false;
        try {
            String key = "sso2:session:ticket:" + ticket;
            String v = redis.get(key);
            if (StringUtils.hasText(v)) {
                redis.del(key);
                removed = true;
            }
        } catch (Exception ignore) {}
        synchronized (LOCAL_SESSION) {
            if (LOCAL_SESSION.remove(ticket) != null) {
                removed = true;
            }
        }
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err("ticket not found"));
        }
        return ResponseEntity.ok(ok("logout"));
    }

    /**
     * 查询用户信息
     */
    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@RequestHeader(name = "Authorization", required = false) String auth) {
        String ticket = parseTicket(auth);
        if (!StringUtils.hasText(ticket)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("no ticket"));
        }
        String username = null;
        // 先查 Redis
        try {
            username = redis.get("sso2:session:ticket:" + ticket);
        } catch (Exception ignore) {}
        // 回落查内存
        if (!StringUtils.hasText(username)) {
            synchronized (LOCAL_SESSION) {
                SessionEntry entry = LOCAL_SESSION.get(ticket);
                if (entry != null && entry.expireAt > System.currentTimeMillis()) {
                    username = entry.username;
                } else {
                    LOCAL_SESSION.remove(ticket);
                }
            }
        }
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid or expired ticket"));
        }
        Map<String, Object> ret = new HashMap<>();
        ret.put("username", username);
        ret.put("roles", Arrays.asList("USER"));
        return ResponseEntity.ok(ret);
    }

    private String parseTicket(String auth) {
        if (!StringUtils.hasText(auth)) return null;
        String prefix = "Ticket ";
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

    private static class SessionEntry {
        String username;
        long expireAt;
        SessionEntry(String username, long expireAt) {
            this.username = username;
            this.expireAt = expireAt;
        }
    }
}