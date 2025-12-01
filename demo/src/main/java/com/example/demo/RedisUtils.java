package com.example.demo;

import org.springframework.data.redis.core.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，封装常用操作，支持静态调用。
 */
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * 初始化RedisTemplate，需在应用启动时调用一次
     */
    public static void init(RedisTemplate<String, Object> template) {
        redisTemplate = template;
    }

    private static void checkTemplate() {
        if (redisTemplate == null) {
            System.err.println("RedisUtils.checkTemplate: RedisTemplate未初始化");
            throw new IllegalStateException("RedisTemplate未初始化，请先调用RedisUtils.init()注入RedisTemplate实例");
        }
    }

    /** 设置字符串 */
    public static void set(String key, Object value) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        redisTemplate.opsForValue().set(key, value);
    }

    /** 设置字符串并指定过期时间（秒） */
    public static void set(String key, Object value, long timeoutSeconds) {
        checkTemplate();
        if (CommonUtils.isEmpty(key) || timeoutSeconds <= 0) throw new IllegalArgumentException("key不能为空，timeoutSeconds需大于0");
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /** 获取字符串 */
    public static Object get(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除key */
    public static void delete(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        redisTemplate.delete(key);
    }

    /** 设置key过期时间（秒） */
    public static boolean expire(String key, long timeoutSeconds) {
        checkTemplate();
        if (CommonUtils.isEmpty(key) || timeoutSeconds <= 0) throw new IllegalArgumentException("key不能为空，timeoutSeconds需大于0");
        return redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    /** 获取key剩余过期时间（秒） */
    public static long getExpire(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /** 判断key是否存在 */
    public static boolean hasKey(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.hasKey(key);
    }

    /** 哈希操作：设置 */
    public static void hSet(String key, String field, Object value) {
        checkTemplate();
        if (CommonUtils.isEmpty(key) || CommonUtils.isEmpty(field)) throw new IllegalArgumentException("key和field不能为空");
        redisTemplate.opsForHash().put(key, field, value);
    }

    /** 哈希操作：获取 */
    public static Object hGet(String key, String field) {
        checkTemplate();
        if (CommonUtils.isEmpty(key) || CommonUtils.isEmpty(field)) throw new IllegalArgumentException("key和field不能为空");
        return redisTemplate.opsForHash().get(key, field);
    }

    /** 哈希操作：获取所有 */
    public static Map<Object, Object> hGetAll(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.opsForHash().entries(key);
    }

    /** 列表操作：左推入 */
    public static void lPush(String key, Object value) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        redisTemplate.opsForList().leftPush(key, value);
    }

    /** 列表操作：右弹出 */
    public static Object rPop(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.opsForList().rightPop(key);
    }

    /** 集合操作：添加 */
    public static void sAdd(String key, Object value) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        redisTemplate.opsForSet().add(key, value);
    }

    /** 集合操作：获取所有 */
    public static Set<Object> sMembers(String key) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.opsForSet().members(key);
    }

    /** 获取列表区间 */
    public static List<Object> lRange(String key, long start, long end) {
        checkTemplate();
        if (CommonUtils.isEmpty(key)) throw new IllegalArgumentException("key不能为空");
        return redisTemplate.opsForList().range(key, start, end);
    }
}
