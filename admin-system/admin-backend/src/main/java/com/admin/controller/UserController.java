package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.User;
import com.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String username,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) Long deptId) {
        List<User> list = userService.getList(username, status, deptId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> add(@RequestBody User user) {
        try {
            userService.add(user);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.update(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        userService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getRoleIds(@PathVariable Long userId) {
        List<Long> roleIds = userService.getRoleIds(userId);
        return Result.success(roleIds);
    }

    @PutMapping("/{userId}/roles")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error("新密码不能为空");
        }
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
}
