package com.example.demo;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 用于管理 JWT Token 的黑名单和缓存
 */
@Service
public class JwtCacheService {

    private static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String TOKEN_CACHE_PREFIX = "jwt:cache:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 缓存 token
     * @param username 用户名
     * @param token token字符串
     * @param expireMillis 过期时间（毫秒），建议与 token 有效期一致
     */
    public void setToken(String username, String token, long expireMillis) {
        redisTemplate.opsForValue().set(TOKEN_CACHE_PREFIX + username, token, expireMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取缓存的 token
     * @param username 用户名
     * @return token字符串或null
     */
    public String getToken(String username) {
        return redisTemplate.opsForValue().get(TOKEN_CACHE_PREFIX + username);
    }

    /**
     * 删除缓存的 token
     * @param username 用户名
     */
    public void deleteToken(String username) {
        redisTemplate.delete(TOKEN_CACHE_PREFIX + username);
    }

    /**
     * 将 token 加入黑名单
     * @param token token字符串
     * @param expireMillis 过期时间（毫秒），建议与 token 有效期一致
     */
    public void blacklistToken(String token, long expireMillis) {
        redisTemplate.opsForValue().set(TOKEN_BLACKLIST_PREFIX + token, "1", expireMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 判断 token 是否在黑名单
     * @param token token字符串
     * @return true: 已失效/拉黑，false: 有效
     */
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token);
    }
}