package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.dto.UserCreateRequest;
import com.admin.dto.UserUpdateRequest;
import com.admin.entity.User;

import java.util.List;

public interface UserService {

    PageResult<User> selectUserList(PageParam pageParam, String username, Integer status, Long deptId);

    User selectUserById(Long id);

    void createUser(UserCreateRequest request);

    void updateUser(UserUpdateRequest request);

    void deleteUser(Long id);

    void resetPassword(Long userId, String newPassword);

    void updateProfile(User user);

    void updatePassword(Long userId, String oldPassword, String newPassword);
}
