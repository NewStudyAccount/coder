package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Menu;
import com.admin.entity.User;
import com.admin.service.AuthService;
import com.admin.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MenuService menuService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }
        try {
            Map<String, Object> data = authService.login(username, password);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        try {
            authService.register(user);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/userinfo")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        User user = authService.getCurrentUser(userId);
        return Result.success(user);
    }

    @GetMapping("/menus")
    public Result<List<Menu>> getUserMenus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<Long> roleIds = authService.getUserRoleIds(userId);
        List<Menu> menus = menuService.getMenusByRoleIds(roleIds);
        return Result.success(menus);
    }
}
