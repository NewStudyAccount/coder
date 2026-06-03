package com.admin.service;

import com.admin.security.LoginUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserOnlineService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_TOKEN_KEY = "login:token:";

    public UserOnlineService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<LoginUser> selectOnlineList(String username, String ipaddr) {
        Set<String> keys = redisTemplate.keys(LOGIN_TOKEN_KEY + "*");
        List<LoginUser> onlineUsers = new ArrayList<>();
        if (keys == null || keys.isEmpty()) {
            return onlineUsers;
        }
        for (String key : keys) {
            LoginUser user = (LoginUser) redisTemplate.opsForValue().get(key);
            if (user != null) {
                if (username != null && !username.isEmpty() && !user.getUsername().contains(username)) {
                    continue;
                }
                onlineUsers.add(user);
            }
        }
        return onlineUsers;
    }

    public void forceLogout(Long userId) {
        redisTemplate.delete(LOGIN_TOKEN_KEY + userId);
    }

    public void refreshUserToken(Long userId, LoginUser loginUser) {
        redisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + userId, loginUser, 30, TimeUnit.MINUTES);
    }
}
