package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.dto.UserCreateRequest;
import com.admin.dto.UserUpdateRequest;
import com.admin.entity.User;
import com.admin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<User>> list(PageParam pageParam,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) Long deptId) {
        return Result.success(userService.selectUserList(pageParam, username, status, deptId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<User> getInfo(@PathVariable Long id) {
        return Result.success(userService.selectUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Void> add(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> edit(@Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/resetPassword")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public Result<Void> resetPassword(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String newPassword = body.getOrDefault("newPassword", "123456");
        userService.resetPassword(userId, newPassword);
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return Result.success();
    }

    @PutMapping("/profile/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
