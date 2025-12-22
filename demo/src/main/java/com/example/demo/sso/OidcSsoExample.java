package com.example.demo.sso;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * OAuth2/OIDC 客户端示例（授权码流程），使用 /sso2/oidc/** 前缀。
 * 说明：
 * - 该示例不依赖 Spring Security OAuth2 Client，纯手写最简流程便于理解与改造。
 * - 需要一个外部 IdP（如 Keycloak/Okta/Google），通过配置 issuer/authorize/token/userinfo 等端点。
 * - 为避免引入 HTTP 客户端库，这里将回调后的 code/state 校验逻辑与前端交互定义清晰，
 *   真正的令牌交换（code -> token）与 userinfo 获取在此示例中用占位方法模拟，你可替换为实际 HTTP 调用。
 *
 * 端点：
 * - GET /sso2/oidc/login?redirect_uri=... ：重定向到 IdP 授权页面
 * - GET /sso2/oidc/callback?code=...&state=... ：IdP 回调，校验 state，交换令牌，返回用户信息
 * - GET /sso2/oidc/userinfo ：根据内存会话或前端持有的 access_token 返回用户信息（示例）
 */
@RestController
@RequestMapping("/sso2/oidc")
public class OidcSsoExample {

    // OIDC 配置（示例，占位）
    private static final String AUTH_SERVER_AUTHORIZE = "https://idp.example.com/oauth2/authorize";
    private static final String AUTH_SERVER_TOKEN = "https://idp.example.com/oauth2/token";
    private static final String AUTH_SERVER_USERINFO = "https://idp.example.com/oauth2/userinfo";
    private static final String CLIENT_ID = "demo-client-id";
    private static final String CLIENT_SECRET = "demo-client-secret";
    private static final String SCOPE = "openid profile email";
    private static final String RESPONSE_TYPE = "code";

    // 内存 state 存储：state -> nonce/expireAt
    private static final Map<String, StateEntry> STATE_STORE = new HashMap<>();
    private static final long STATE_EXPIRE_MS = 5 * 60 * 1000; // 5分钟

    // 简易内存会话：sessionId -> tokenInfo
    private static final Map<String, TokenInfo> SESSION_STORE = new HashMap<>();
    private static final long SESSION_EXPIRE_MS = 2 * 60 * 60 * 1000; // 2小时

    private static final SecureRandom RAND = new SecureRandom();

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String redirect_uri) {
        if (!StringUtils.hasText(redirect_uri)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("missing redirect_uri"));
        }
        String state = genRandomUrlSafe(24);
        String nonce = genRandomUrlSafe(24);
        synchronized (STATE_STORE) {
            STATE_STORE.put(state, new StateEntry(nonce, System.currentTimeMillis() + STATE_EXPIRE_MS));
        }
        String authUrl = AUTH_SERVER_AUTHORIZE
                + "?response_type=" + url(RESPONSE_TYPE)
                + "&client_id=" + url(CLIENT_ID)
                + "&redirect_uri=" + url(redirect_uri)
                + "&scope=" + url(SCOPE)
                + "&state=" + url(state)
                + "&nonce=" + url(nonce);

        Map<String, Object> res = new HashMap<>();
        res.put("authorize_url", authUrl);
        res.put("state", state);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(@RequestParam String code,
                                                        @RequestParam String state,
                                                        @RequestParam(required = false) String redirect_uri) {
        if (!StringUtils.hasText(code) || !StringUtils.hasText(state)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("missing code/state"));
        }
        StateEntry se;
        synchronized (STATE_STORE) {
            se = STATE_STORE.remove(state);
        }
        if (se == null || se.expireAt < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid state"));
        }

        // 交换令牌（模拟/占位）：实际需用 HTTP POST 到 AUTH_SERVER_TOKEN
        TokenInfo token = exchangeCodeForToken(code, redirect_uri);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("token exchange failed"));
        }

        // 拉取用户信息（模拟/占位）：实际需用 HTTP GET 到 AUTH_SERVER_USERINFO 携带 access_token
        Map<String, Object> userinfo = fetchUserinfo(token.accessToken);
        if (userinfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("fetch userinfo failed"));
        }

        // 生成本地会话
        String sessionId = genRandomUrlSafe(32);
        synchronized (SESSION_STORE) {
            SESSION_STORE.put(sessionId, token);
        }
        Map<String, Object> ret = new HashMap<>();
        ret.put("session_id", sessionId);
        ret.put("access_token", token.accessToken);
        ret.put("id_token", token.idToken);
        ret.put("expires_in", token.expiresIn);
        ret.put("userinfo", userinfo);
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@RequestHeader(name = "X-Session-Id", required = false) String sessionId,
                                                        @RequestHeader(name = "Authorization", required = false) String auth) {
        // 优先通过会话
        if (StringUtils.hasText(sessionId)) {
            TokenInfo t;
            synchronized (SESSION_STORE) {
                t = SESSION_STORE.get(sessionId);
                if (t != null && t.expireAt < System.currentTimeMillis()) {
                    SESSION_STORE.remove(sessionId);
                    t = null;
                }
            }
            if (t != null && StringUtils.hasText(t.accessToken)) {
                Map<String, Object> info = fetchUserinfo(t.accessToken);
                if (info != null) return ResponseEntity.ok(info);
            }
        }
        // 或者直接带 Bearer access_token
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length());
            Map<String, Object> info = fetchUserinfo(token);
            if (info != null) return ResponseEntity.ok(info);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("no valid session/token"));
    }

    // =========== 占位的令牌交换和用户信息获取（请替换为真实 HTTP 调用） ===========
    private TokenInfo exchangeCodeForToken(String code, String redirectUri) {
        // 真实实现：HTTP POST 到 AUTH_SERVER_TOKEN，传 client_id/secret/code/redirect_uri/grant_type=authorization_code
        // 这里模拟返回
        TokenInfo t = new TokenInfo();
        t.accessToken = "mock_access_" + code;
        t.idToken = "mock_id_token_" + code;
        t.expiresIn = 3600;
        t.expireAt = System.currentTimeMillis() + t.expiresIn * 1000L;
        return t;
    }

    private Map<String, Object> fetchUserinfo(String accessToken) {
        // 真实实现：HTTP GET 到 AUTH_SERVER_USERINFO，Header: Authorization: Bearer {accessToken}
        // 这里模拟返回
        Map<String, Object> info = new HashMap<>();
        info.put("sub", "user-" + accessToken.substring(Math.max(0, accessToken.length() - 8)));
        info.put("name", "Demo User");
        info.put("email", "demo@example.com");
        info.put("roles", Arrays.asList("USER"));
        return info;
    }

    // =========== 工具 ===========
    private String genRandomUrlSafe(int len) {
        byte[] buf = new byte[len];
        RAND.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
    private String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
    private Map<String, Object> err(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }

    private static class StateEntry {
        String nonce;
        long expireAt;
        StateEntry(String nonce, long expireAt) { this.nonce = nonce; this.expireAt = expireAt; }
    }
    private static class TokenInfo {
        String accessToken;
        String idToken;
        int expiresIn;
        long expireAt;
    }
}