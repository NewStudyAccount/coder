package com.example.demo.oidc;

import com.example.demo.RedisUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * 简化版 OIDC 提供方服务（授权码 + 刷新令牌），端点前缀 /oidc2/**
 * 新增：第三方登录支持（以 GitHub/Google 为例），通过 /oidc2/external/{provider}/login 与 /oidc2/external/{provider}/callback
 * 将外部身份映射为本地用户后，颁发我方授权码并走标准 /token 流程。
 */
@RestController
public class OidcServer {

    private final RedisUtils redis;

    public OidcServer(RedisUtils redis) {
        this.redis = redis;
    }

    // ============== 配置与常量 ==============
    private static final String ISSUER = "http://localhost:8080";
    private static final long CODE_EXPIRE_SEC = 300;
    private static final long ACCESS_EXPIRE_SEC = 3600;
    private static final long REFRESH_EXPIRE_SEC = 7 * 24 * 3600;

    // 客户端注册（内存）
    private static final Map<String, Client> CLIENTS = new HashMap<>();
    static {
        CLIENTS.put("demo-client", new Client("demo-client", "demo-secret", new HashSet<>(Arrays.asList(
                "http://localhost:5173/callback",
                "http://localhost:3000/callback",
                "http://localhost:8080/callback"
        ))));
    }

    // 简单用户表（本地账户）
    private static final Map<String, String> USER_DB = new HashMap<>();
    static {
        USER_DB.put("alice", "alice123");
        USER_DB.put("bob", "bob123");
    }

    // 外部身份绑定：external provider + external sub/email -> local username
    private static final Map<String, String> EXTERNAL_BINDING = new HashMap<>();

    // 内存回落存储
    private static final Map<String, CodeEntry> LOCAL_CODE = new HashMap<>();
    private static final Map<String, TokenEntry> LOCAL_TOKEN = new HashMap<>();

    // ============== RSA 密钥与 JWKS ==============
    private static final RSAKeys RSA = RSAKeys.generate();
    private static final String JWK_KID = UUID.randomUUID().toString();

    // ============== 发现端点 ==============
    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> discovery() {
        Map<String, Object> m = new LinkedHashMap<>();
        String base = ISSUER;
        m.put("issuer", base);
        m.put("authorization_endpoint", base + "/oidc2/authorize");
        m.put("token_endpoint", base + "/oidc2/token");
        m.put("userinfo_endpoint", base + "/oidc2/userinfo");
        m.put("jwks_uri", base + "/oidc2/jwks.json");
        m.put("response_types_supported", Arrays.asList("code"));
        m.put("subject_types_supported", Arrays.asList("public"));
        m.put("id_token_signing_alg_values_supported", Arrays.asList("RS256"));
        m.put("scopes_supported", Arrays.asList("openid", "profile", "email"));
        m.put("grant_types_supported", Arrays.asList("authorization_code", "refresh_token"));
        return m;
    }

    // ============== JWKS ==============
    @GetMapping(value = "/oidc2/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jwks() {
        RSAPublicKey pub = RSA.publicKey;
        Map<String, Object> jwk = new LinkedHashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("kid", JWK_KID);
        jwk.put("alg", "RS256");
        jwk.put("use", "sig");
        jwk.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(pub.getModulus().toByteArray()).replaceAll("^\u0000", ""));
        jwk.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(pub.getPublicExponent().toByteArray()));
        Map<String, Object> jwks = new HashMap<>();
        jwks.put("keys", Collections.singletonList(jwk));
        return jwks;
    }

    // ============== 授权端点（简化认证：username/password 或走外部登录） ==============
    @GetMapping("/oidc2/authorize")
    public ResponseEntity<?> authorize(@RequestParam String response_type,
                                       @RequestParam String client_id,
                                       @RequestParam String redirect_uri,
                                       @RequestParam(required = false) String scope,
                                       @RequestParam(required = false) String state,
                                       @RequestParam(required = false) String username,
                                       @RequestParam(required = false) String password,
                                       @RequestParam(required = false, name = "use_external") String useExternal,
                                       @RequestParam(required = false, name = "provider") String provider) {
        // 基本校验
        if (!"code".equals(response_type)) {
            return errorRedirect(redirect_uri, state, "unsupported_response_type");
        }
        Client client = CLIENTS.get(client_id);
        if (client == null || !client.redirectUris.contains(redirect_uri)) {
            return errorRedirect(redirect_uri, state, "unauthorized_client");
        }

        // 外部登录模式：当 use_external=true 时，跳转到外部授权
        if ("true".equalsIgnoreCase(useExternal)) {
            if (!StringUtils.hasText(provider)) {
                return errorRedirect(redirect_uri, state, "invalid_external_provider");
            }
            String externalLogin = externalAuthorizeUrl(provider, client_id, redirect_uri, scopeOrDefault(scope), state);
            if (externalLogin == null) {
                return errorRedirect(redirect_uri, state, "unsupported_external_provider");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(externalLogin));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        // 简化用户名密码认证（本地）
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            // 提示缺少凭据
            return errorRedirect(redirect_uri, state, "login_required");
        }
        String pwd = USER_DB.get(username);
        if (pwd == null || !pwd.equals(password)) {
            return errorRedirect(redirect_uri, state, "access_denied");
        }
        // 颁发授权码
        String code = genCode();
        long expireAt = now() + CODE_EXPIRE_SEC;
        CodeEntry ce = new CodeEntry(client_id, redirect_uri, username, scopeOrDefault(scope), expireAt);
        saveCode(code, ce, CODE_EXPIRE_SEC);

        URI uri = URI.create(redirect_uri + "?code=" + code + (StringUtils.hasText(state) ? "&state=" + state : ""));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ============== 外部登录入口与回调（GitHub / Google 示例） ==============
    // GitHub OAuth2
    private static final String GH_AUTHORIZE = "https://github.com/login/oauth/authorize";
    private static final String GH_TOKEN = "https://github.com/login/oauth/access_token";
    private static final String GH_USER = "https://api.github.com/user";
    // Google OAuth2（示例）：
    private static final String GOOGLE_AUTHORIZE = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO = "https://openidconnect.googleapis.com/v1/userinfo";

    // 外部 provider 配置（请替换为真实 client_id/secret 与回调地址）
    private static final Map<String, ExternalProviderConfig> EXTERNAL_PROVIDERS = new HashMap<>();
    static {
        EXTERNAL_PROVIDERS.put("github", new ExternalProviderConfig(
                "your-github-client-id",
                "your-github-client-secret",
                ISSUER + "/oidc2/external/github/callback",
                "read:user user:email",
                "code"
        ));
        EXTERNAL_PROVIDERS.put("google", new ExternalProviderConfig(
                "your-google-client-id",
                "your-google-client-secret",
                ISSUER + "/oidc2/external/google/callback",
                "openid email profile",
                "code"
        ));
    }

    // 跳转外部登录
    @GetMapping("/oidc2/external/{provider}/login")
    public ResponseEntity<?> externalLogin(@PathVariable String provider,
                                           @RequestParam String client_id,
                                           @RequestParam String redirect_uri,
                                           @RequestParam(required = false) String scope,
                                           @RequestParam(required = false) String state) {
        String url = externalAuthorizeUrl(provider, client_id, redirect_uri, scopeOrDefault(scope), state);
        if (url == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("unsupported_external_provider"));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 外部回调（示例：GitHub）
    @GetMapping("/oidc2/external/github/callback")
    public ResponseEntity<?> externalGithubCallback(@RequestParam String code,
                                                    @RequestParam(required = false) String state,
                                                    @RequestParam(required = false) String client_id,
                                                    @RequestParam(required = false) String redirect_uri) {
        // 使用 code 换取外部 access_token 与用户信息（此处用占位模拟；生产需 HTTP 请求）
        Map<String, Object> ghUser = mockGithubUser(code);
        if (ghUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("github_exchange_failed"));
        }
        String externalSub = String.valueOf(ghUser.getOrDefault("id", "unknown"));
        String localUser = bindOrCreateLocal("github", externalSub, (String) ghUser.getOrDefault("login", "gh_user"));

        // 为本地 client_id/redirect_uri 颁发授权码，并重定向回去
        if (!StringUtils.hasText(client_id)) client_id = "demo-client";
        if (!StringUtils.hasText(redirect_uri)) redirect_uri = "http://localhost:5173/callback";
        Client client = CLIENTS.get(client_id);
        if (client == null || !client.redirectUris.contains(redirect_uri)) {
            return errorRedirect(redirect_uri, state, "unauthorized_client");
        }
        String codeLocal = genCode();
        CodeEntry ce = new CodeEntry(client_id, redirect_uri, localUser, "openid profile email", now() + CODE_EXPIRE_SEC);
        saveCode(codeLocal, ce, CODE_EXPIRE_SEC);

        URI uri = URI.create(redirect_uri + "?code=" + codeLocal + (StringUtils.hasText(state) ? "&state=" + state : ""));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 外部回调（示例：Google）
    @GetMapping("/oidc2/external/google/callback")
    public ResponseEntity<?> externalGoogleCallback(@RequestParam String code,
                                                    @RequestParam(required = false) String state,
                                                    @RequestParam(required = false) String client_id,
                                                    @RequestParam(required = false) String redirect_uri) {
        Map<String, Object> ggUser = mockGoogleUser(code);
        if (ggUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("google_exchange_failed"));
        }
        String externalSub = String.valueOf(ggUser.getOrDefault("sub", "unknown"));
        String email = (String) ggUser.getOrDefault("email", "gg_user@example.com");
        String localUser = bindOrCreateLocal("google", externalSub, email);

        if (!StringUtils.hasText(client_id)) client_id = "demo-client";
        if (!StringUtils.hasText(redirect_uri)) redirect_uri = "http://localhost:5173/callback";
        Client client = CLIENTS.get(client_id);
        if (client == null || !client.redirectUris.contains(redirect_uri)) {
            return errorRedirect(redirect_uri, state, "unauthorized_client");
        }
        String codeLocal = genCode();
        CodeEntry ce = new CodeEntry(client_id, redirect_uri, localUser, "openid profile email", now() + CODE_EXPIRE_SEC);
        saveCode(codeLocal, ce, CODE_EXPIRE_SEC);

        URI uri = URI.create(redirect_uri + "?code=" + codeLocal + (StringUtils.hasText(state) ? "&state=" + state : ""));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private String externalAuthorizeUrl(String provider, String clientId, String redirectUri, String scope, String state) {
        ExternalProviderConfig cfg = EXTERNAL_PROVIDERS.get(provider);
        if (cfg == null) return null;
        if ("github".equals(provider)) {
            StringBuilder sb = new StringBuilder(GH_AUTHORIZE)
                    .append("?client_id=").append(url(cfg.clientId))
                    .append("&redirect_uri=").append(url(cfg.redirectUri))
                    .append("&scope=").append(url(cfg.scope))
                    .append("&state=").append(url(encodeState(clientId, redirectUri, state)));
            return sb.toString();
        } else if ("google".equals(provider)) {
            StringBuilder sb = new StringBuilder(GOOGLE_AUTHORIZE)
                    .append("?response_type=code")
                    .append("&client_id=").append(url(cfg.clientId))
                    .append("&redirect_uri=").append(url(cfg.redirectUri))
                    .append("&scope=").append(url(cfg.scope))
                    .append("&access_type=offline")
                    .append("&include_granted_scopes=true")
                    .append("&state=").append(url(encodeState(clientId, redirectUri, state)));
            return sb.toString();
        }
        return null;
    }

    private String encodeState(String clientId, String redirectUri, String state) {
        // 将本地 client 信息打进 state，便于回调后还原
        String raw = (StringUtils.hasText(clientId) ? clientId : "") + "|" + (StringUtils.hasText(redirectUri) ? redirectUri : "") + "|" + (StringUtils.hasText(state) ? state : "");
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        }

    private DecodedState decodeState(String s) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(s), StandardCharsets.UTF_8);
            String[] a = raw.split("\\|", -1);
            return new DecodedState(a.length > 0 ? a[0] : "", a.length > 1 ? a[1] : "", a.length > 2 ? a[2] : "");
        } catch (Exception e) {
            return new DecodedState("", "", "");
        }
    }

    private Map<String, Object> mockGithubUser(String code) {
        // 真实实现：POST GH_TOKEN 交换 access_token，再 GET GH_USER 获取用户信息
        Map<String, Object> u = new HashMap<>();
        u.put("id", "gh-" + code.substring(0, Math.min(8, code.length())));
        u.put("login", "github_user_" + code.substring(0, Math.min(5, code.length())));
        return u;
    }
    private Map<String, Object> mockGoogleUser(String code) {
        // 真实实现：POST GOOGLE_TOKEN 交换 access_token，再 GET GOOGLE_USERINFO 获取用户信息
        Map<String, Object> u = new HashMap<>();
        u.put("sub", "gg-" + code.substring(0, Math.min(8, code.length())));
        u.put("email", "gg_user_" + code.substring(0, Math.min(5, code.length())) + "@example.com");
        u.put("name", "Google User");
        return u;
    }

    private String bindOrCreateLocal(String provider, String externalSub, String suggestedName) {
        String key = provider + ":" + externalSub;
        String local = EXTERNAL_BINDING.get(key);
        if (StringUtils.hasText(local)) return local;
        // 创建影子用户（本地用户名）
        local = (suggestedName != null ? suggestedName : provider + "_" + externalSub);
        if (!USER_DB.containsKey(local)) {
            USER_DB.put(local, UUID.randomUUID().toString()); // 生成一个随机密码（不会在本地登录中使用）
        }
        EXTERNAL_BINDING.put(key, local);
        return local;
    }

    // ============== 令牌端点 ==============
    @PostMapping(value = "/oidc2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> token(@RequestParam("grant_type") String grantType,
                                                     @RequestParam Map<String, String> params,
                                                     @RequestHeader(value = "Authorization", required = false) String auth) {
        if ("authorization_code".equals(grantType)) {
            return handleAuthorizationCode(params, auth);
        } else if ("refresh_token".equals(grantType)) {
            return handleRefreshToken(params, auth);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("unsupported_grant_type"));
    }

    private ResponseEntity<Map<String, Object>> handleAuthorizationCode(Map<String, String> params, String auth) {
        String code = params.get("code");
        String redirectUri = params.get("redirect_uri");
        String clientId = params.get("client_id");
        String clientSecret = params.get("client_secret");

        // 支持 Basic 认证
        if (!StringUtils.hasText(clientId)) {
            String[] basic = parseBasic(auth);
            if (basic != null) {
                clientId = basic[0];
                clientSecret = basic[1];
            }
        }
        Client client = CLIENTS.get(clientId);
        if (client == null || !Objects.equals(client.secret, clientSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid_client"));
        }
        CodeEntry ce = getAndRemoveCode(code);
        if (ce == null || ce.expireAt < now() || !Objects.equals(ce.clientId, clientId) || !Objects.equals(ce.redirectUri, redirectUri)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("invalid_grant"));
        }
        // 颁发访问令牌与ID令牌，及刷新令牌
        TokenEntry te = issueTokens(clientId, ce.username, ce.scope);
        saveToken(te, ACCESS_EXPIRE_SEC, REFRESH_EXPIRE_SEC);

        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("access_token", te.accessToken);
        ret.put("token_type", "Bearer");
        ret.put("expires_in", ACCESS_EXPIRE_SEC);
        ret.put("refresh_token", te.refreshToken);
        ret.put("id_token", te.idToken);
        return ResponseEntity.ok(ret);
    }

    private ResponseEntity<Map<String, Object>> handleRefreshToken(Map<String, String> params, String auth) {
        String refreshToken = params.get("refresh_token");
        String clientId = params.get("client_id");
        String clientSecret = params.get("client_secret");
        if (!StringUtils.hasText(clientId)) {
            String[] basic = parseBasic(auth);
            if (basic != null) {
                clientId = basic[0];
                clientSecret = basic[1];
            }
        }
        Client client = CLIENTS.get(clientId);
        if (client == null || !Objects.equals(client.secret, clientSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid_client"));
        }
        TokenEntry te = loadByRefreshToken(refreshToken);
        if (te == null || te.refreshExpireAt < now()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("invalid_grant"));
        }
        // 颁发新的 access_token 与 id_token（保留 refresh_token 或轮换，这里保留不变）
        te = te.rotateAccess(RSA, JWK_KID);
        saveToken(te, ACCESS_EXPIRE_SEC, REFRESH_EXPIRE_SEC);

        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("access_token", te.accessToken);
        ret.put("token_type", "Bearer");
        ret.put("expires_in", ACCESS_EXPIRE_SEC);
        ret.put("refresh_token", te.refreshToken);
        ret.put("id_token", te.idToken);
        return ResponseEntity.ok(ret);
    }

    // ============== 用户信息端点 ==============
    @GetMapping("/oidc2/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@RequestHeader(name = "Authorization", required = false) String auth) {
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid_token"));
        }
        String token = auth.substring("Bearer ".length());
        TokenEntry te = loadByAccessToken(token);
        if (te == null || te.accessExpireAt < now()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("invalid_token"));
        }
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("sub", te.username);
        info.put("name", te.username);
        info.put("email", te.username + "@example.com");
        info.put("preferred_username", te.username);
        info.put("scope", te.scope);
        return ResponseEntity.ok(info);
    }

    // ============== 辅助与存储 ==============
    private Map<String, Object> err(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }

    private static long now() { return System.currentTimeMillis() / 1000; }

    private String scopeOrDefault(String scope) {
        if (!StringUtils.hasText(scope)) return "openid profile email";
        return scope;
    }

    private String genCode() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    private String genToken() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    private String[] parseBasic(String auth) {
        try {
            if (auth != null && auth.startsWith("Basic ")) {
                String b64 = auth.substring("Basic ".length());
                String s = new String(Base64.getDecoder().decode(b64));
                int i = s.indexOf(':');
                if (i > 0) {
                    return new String[]{s.substring(0, i), s.substring(i + 1)};
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private void saveCode(String code, CodeEntry ce, long ttlSec) {
        try {
            redis.setEx("oidc2:code:" + code, ce.toString(), (int) ttlSec);
        } catch (Exception e) {
            synchronized (LOCAL_CODE) {
                LOCAL_CODE.put(code, ce);
            }
        }
    }

    private CodeEntry getAndRemoveCode(String code) {
        try {
            String v = redis.get("oidc2:code:" + code);
            if (StringUtils.hasText(v)) {
                redis.del("oidc2:code:" + code);
                return CodeEntry.fromString(v);
            }
        } catch (Exception ignore) {}
        synchronized (LOCAL_CODE) {
            return LOCAL_CODE.remove(code);
        }
    }

    private void saveToken(TokenEntry te, long accessTtlSec, long refreshTtlSec) {
        try {
            redis.setEx("oidc2:access:" + te.accessToken, te.key(), (int) accessTtlSec);
            redis.setEx("oidc2:refresh:" + te.refreshToken, te.key(), (int) refreshTtlSec);
        } catch (Exception e) {
            synchronized (LOCAL_TOKEN) {
                LOCAL_TOKEN.put(te.accessToken, te);
                LOCAL_TOKEN.put(te.refreshToken, te);
            }
        }
    }

    private TokenEntry loadByAccessToken(String accessToken) {
        try {
            String key = redis.get("oidc2:access:" + accessToken);
            if (StringUtils.hasText(key)) {
                return TokenEntry.fromKey(key);
            }
        } catch (Exception ignore) {}
        synchronized (LOCAL_TOKEN) {
            return LOCAL_TOKEN.get(accessToken);
        }
    }

    private TokenEntry loadByRefreshToken(String refreshToken) {
        try {
            String key = redis.get("oidc2:refresh:" + refreshToken);
            if (StringUtils.hasText(key)) {
                return TokenEntry.fromKey(key);
            }
        } catch (Exception ignore) {}
        synchronized (LOCAL_TOKEN) {
            return LOCAL_TOKEN.get(refreshToken);
        }
    }

    private TokenEntry issueTokens(String clientId, String username, String scope) {
        long now = now();
        long exp = now + ACCESS_EXPIRE_SEC;
        String jti = UUID.randomUUID().toString();

        // access_token 这里用随机串（也可用 JWT），id_token 使用 JWT RS256
        String access = genToken();
        String refresh = genToken();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", JWK_KID)
                .setIssuer(ISSUER)
                .setSubject(username)
                .setAudience(clientId)
                .claim("scope", scope)
                .setId(jti)
                .setIssuedAt(new Date(now * 1000))
                .setExpiration(new Date(exp * 1000))
                .signWith(RSA.privateKey, SignatureAlgorithm.RS256)
                .compact();

        TokenEntry te = new TokenEntry();
        te.clientId = clientId;
        te.username = username;
        te.scope = scope;
        te.accessToken = access;
        te.accessExpireAt = exp;
        te.refreshToken = refresh;
        te.refreshExpireAt = now + REFRESH_EXPIRE_SEC;
        te.idToken = idToken;
        return te;
    }

    // ============== 内部类 ==============
    private static class Client {
        String id;
        String secret;
        Set<String> redirectUris;
        Client(String id, String secret, Set<String> redirectUris) {
            this.id = id; this.secret = secret; this.redirectUris = redirectUris;
        }
    }

    private static class CodeEntry {
        String clientId;
        String redirectUri;
        String username;
        String scope;
        long expireAt;
        CodeEntry(String clientId, String redirectUri, String username, String scope, long expireAt) {
            this.clientId = clientId; this.redirectUri = redirectUri; this.username = username; this.scope = scope; this.expireAt = expireAt;
        }
        public String toString() {
            return clientId + "|" + redirectUri + "|" + username + "|" + scope + "|" + expireAt;
        }
        static CodeEntry fromString(String s) {
            String[] a = s.split("\\|", -1);
            return new CodeEntry(a[0], a[1], a[2], a[3], Long.parseLong(a[4]));
        }
    }

    private static class TokenEntry {
        String clientId;
        String username;
        String scope;
        String accessToken;
        long accessExpireAt;
        String refreshToken;
        long refreshExpireAt;
        String idToken;

        String key() {
            // 为了简化跨 Redis/内存的序列化，这里用 '||' 连接
            return clientId + "||" + username + "||" + scope + "||" + accessToken + "||" + accessExpireAt + "||" + refreshToken + "||" + refreshExpireAt + "||" + idToken;
        }
        static TokenEntry fromKey(String key) {
            String[] a = key.split("\\|\\|", -1);
            TokenEntry t = new TokenEntry();
            t.clientId = a[0];
            t.username = a[1];
            t.scope = a[2];
            t.accessToken = a[3];
            t.accessExpireAt = Long.parseLong(a[4]);
            t.refreshToken = a[5];
            t.refreshExpireAt = Long.parseLong(a[6]);
            t.idToken = a[7];
            return t;
        }

        TokenEntry rotateAccess(RSAKeys rsa, String kid) {
            long now = now();
            long exp = now + ACCESS_EXPIRE_SEC;
            this.accessToken = UUID.randomUUID().toString().replace("-", "");
            this.accessExpireAt = exp;
            // 重新签发 id_token
            this.idToken = Jwts.builder()
                    .setHeaderParam("kid", kid)
                    .setIssuer(ISSUER)
                    .setSubject(this.username)
                    .setAudience(this.clientId)
                    .claim("scope", this.scope)
                    .setId(UUID.randomUUID().toString())
                    .setIssuedAt(new Date(now * 1000))
                    .setExpiration(new Date(exp * 1000))
                    .signWith(rsa.privateKey, SignatureAlgorithm.RS256)
                    .compact();
            return this;
        }
    }

    private static class RSAKeys {
        RSAPublicKey publicKey;
        RSAPrivateKey privateKey;
        static RSAKeys generate() {
            try {
                KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
                gen.initialize(2048);
                KeyPair kp = gen.generateKeyPair();
                RSAKeys r = new RSAKeys();
                r.publicKey = (RSAPublicKey) kp.getPublic();
                r.privateKey = (RSAPrivateKey) kp.getPrivate();
                return r;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ExternalProviderConfig {
        String clientId;
        String clientSecret;
        String redirectUri;
        String scope;
        String responseType;
        ExternalProviderConfig(String clientId, String clientSecret, String redirectUri, String scope, String responseType) {
            this.clientId = clientId; this.clientSecret = clientSecret; this.redirectUri = redirectUri; this.scope = scope; this.responseType = responseType;
        }
    }

    private static class DecodedState {
        String clientId;
        String redirectUri;
        String state;
        DecodedState(String clientId, String redirectUri, String state) { this.clientId = clientId; this.redirectUri = redirectUri; this.state = state; }
    }

    // ============== 简单重定向错误辅助 ==============
    private ResponseEntity<?> errorRedirect(String redirectUri, String state, String error) {
        if (!StringUtils.hasText(redirectUri)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err(error));
        }
        String target = redirectUri + "?error=" + error + (StringUtils.hasText(state) ? "&state=" + state : "");
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(target));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ============== 工具 ==============
    private String url(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}