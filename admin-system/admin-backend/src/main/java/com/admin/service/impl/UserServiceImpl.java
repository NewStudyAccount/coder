package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BusinessException;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.dto.UserCreateRequest;
import com.admin.dto.UserUpdateRequest;
import com.admin.entity.*;
import com.admin.mapper.*;
import com.admin.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserPostMapper userPostMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<User> selectUserList(PageParam pageParam, String username, Integer status, Long deptId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(username), User::getUsername, username)
               .eq(status != null, User::getStatus, status)
               .eq(deptId != null, User::getDeptId, deptId)
               .orderByDesc(User::getCreateTime);

        Page<User> page = userMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public User selectUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest request) {
        User exist = userMapper.selectByUsername(request.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDeptId(request.getDeptId());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setRemark(request.getRemark());
        user.setCreateBy("system");
        userMapper.insert(user);

        saveUserRoles(user.getId(), request.getRoleIds());
        saveUserPosts(user.getId(), request.getPostIds());
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateRequest request) {
        User user = userMapper.selectById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getDeptId() != null) user.setDeptId(request.getDeptId());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getRemark() != null) user.setRemark(request.getRemark());
        user.setUpdateBy("system");
        userMapper.updateById(user);

        if (request.getRoleIds() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, request.getId()));
            saveUserRoles(request.getId(), request.getRoleIds());
        }
        if (request.getPostIds() != null) {
            userPostMapper.delete(new LambdaQueryWrapper<UserPost>().eq(UserPost::getUserId, request.getId()));
            saveUserPosts(request.getId(), request.getPostIds());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (id == 1L) {
            throw new BusinessException("不允许删除超级管理员");
        }
        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
        userPostMapper.delete(new LambdaQueryWrapper<UserPost>().eq(UserPost::getUserId, id));
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateBy("system");
        userMapper.updateById(user);
    }

    @Override
    public void updateProfile(User user) {
        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }

    private void saveUserPosts(Long userId, List<Long> postIds) {
        if (postIds != null && !postIds.isEmpty()) {
            for (Long postId : postIds) {
                UserPost up = new UserPost();
                up.setUserId(userId);
                up.setPostId(postId);
                userPostMapper.insert(up);
            }
        }
    }
}
