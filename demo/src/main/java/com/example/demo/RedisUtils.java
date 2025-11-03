package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，封装常用操作
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置字符串
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串并指定过期时间（秒）
     */
    public void set(String key, Object value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取字符串
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置key过期时间（秒）
     */
    public boolean expire(String key, long timeoutSeconds) {
        return redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取key剩余过期时间（秒）
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 哈希操作：设置
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 哈希操作：获取
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 哈希操作：获取所有
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 列表操作：左推入
     */
    public void lPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 列表操作：右弹出
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 集合操作：添加
     */
    public void sAdd(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 集合操作：获取所有
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 获取列表区间
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
}