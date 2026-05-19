package com.admin.service;

import com.admin.entity.User;
import com.admin.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getList(String username, Integer status, Long deptId) {
        return userMapper.selectList(username, status, deptId);
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Transactional
    public void add(User user) {
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(user.getStatus() != null ? user.getStatus() : 1);
        userMapper.insert(user);

        if (user.getRoleKey() != null) {
            userMapper.deleteUserRolesByUserId(user.getId());
        }
    }

    @Transactional
    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.update(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userMapper.deleteUserRolesByUserId(id);
        userMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            userMapper.deleteUserRolesByUserId(id);
        }
        userMapper.deleteBatch(ids);
    }

    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userMapper.deleteUserRolesByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
    }

    public List<Long> getRoleIds(Long userId) {
        return userMapper.selectRoleIdsByUserId(userId);
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
    }
}
