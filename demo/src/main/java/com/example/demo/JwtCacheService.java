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

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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