package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            result.put("code", 401);
            result.put("msg", "未认证");
            return result;
        }
        result.put("code", 0);
        result.put("msg", "success");
        result.put("username", authentication.getName());
        result.put("roles", authentication.getAuthorities());
        return result;
    }

    @GetMapping("/user/list")
    public Map<String, Object> listUsers() {
        List<User> users = userService.listUsers();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", users);
        return result;
    }

    @GetMapping("/user/get")
    public Map<String, Object> getUser(@RequestParam Long id) {
        User user = userService.getUser(id);
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", user);
        }
        return result;
    }

    @PostMapping("/user/add")
    public Map<String, Object> addUser(@RequestBody User user) {
        User newUser = userService.addUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", newUser);
        return result;
    }

    @PostMapping("/user/update")
    public Map<String, Object> updateUser(@RequestParam Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        Map<String, Object> result = new HashMap<>();
        if (updated == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", updated);
        }
        return result;
    }

    @PostMapping("/user/delete")
    public Map<String, Object> deleteUser(@RequestParam Long id) {
        boolean ok = userService.deleteUser(id);
        Map<String, Object> result = new HashMap<>();
        if (ok) {
            result.put("code", 0);
            result.put("msg", "success");
        } else {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        }
        return result;
    }
}