package com.admin.service;

import com.admin.entity.User;
import com.admin.mapper.MenuMapper;
import com.admin.mapper.UserMapper;
import com.admin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String role = user.getRoleKey() != null ? user.getRoleKey() : "user";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("role", role);
        userInfo.put("roleName", user.getRoleName());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("deptName", user.getDeptName());
        result.put("userInfo", userInfo);

        return result;
    }

    @Transactional
    public void register(User user) {
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        userMapper.insert(user);

        userMapper.insertUserRole(user.getId(), 2L);
    }

    public User getCurrentUser(Long userId) {
        return userMapper.selectById(userId);
    }

    public List<Long> getUserRoleIds(Long userId) {
        return userMapper.selectRoleIdsByUserId(userId);
    }
}
