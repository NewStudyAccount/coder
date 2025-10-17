package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@RequestMapping("/api/permissions")
public class UserPermissionController {
    private static final Logger logger = LoggerFactory.getLogger(UserPermissionController.class);

    // 用户权限数据：userId -> 权限列表
    private final Map<Integer, Set<String>> userPermissions = Collections.synchronizedMap(new HashMap<>());
    // 用户数据（仅用于演示）
    private final Map<Integer, String> users = Collections.synchronizedMap(new HashMap<>());

    public UserPermissionController() {
        users.put(1, "张三");
        users.put(2, "李四");
        users.put(3, "王五");
        userPermissions.put(1, new HashSet<>(Arrays.asList("ADMIN", "USER")));
        userPermissions.put(2, new HashSet<>(Arrays.asList("USER")));
        userPermissions.put(3, new HashSet<>(Arrays.asList("GUEST")));
    }

    // 查询所有用户及其权限
    @GetMapping
    public List<UserPermDTO> getAllUserPermissions() {
        logger.info("查询所有用户及其权限");
        List<UserPermDTO> result = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : users.entrySet()) {
            Set<String> perms = userPermissions.getOrDefault(entry.getKey(), new HashSet<>());
            result.add(new UserPermDTO(entry.getKey(), entry.getValue(), new ArrayList<>(perms)));
        }
        logger.info("返回用户权限列表，数量: {}", result.size());
        return result;
    }

    // 查询单个用户权限
    @GetMapping("/{id}")
    public UserPermDTO getUserPermissions(@PathVariable int id) {
        logger.info("查询用户权限，userId={}", id);
        String username = users.get(id);
        if (username == null) {
            logger.error("用户不存在，userId={}", id);
            throw new NoSuchElementException("用户不存在");
        }
        Set<String> perms = userPermissions.getOrDefault(id, new HashSet<>());
        logger.info("用户 {} 权限: {}", username, perms);
        return new UserPermDTO(id, username, new ArrayList<>(perms));
    }

    // 修改用户权限（覆盖式）
    @PutMapping("/{id}")
    public UserPermDTO updateUserPermissions(@PathVariable int id, @RequestBody PermUpdateDTO dto) {
        logger.info("修改用户权限，userId={}, 新权限={}", id, dto.getPermissions());
        if (!users.containsKey(id)) {
            logger.error("用户不存在，userId={}", id);
            throw new NoSuchElementException("用户不存在");
        }
        userPermissions.put(id, new HashSet<>(dto.getPermissions()));
        logger.info("用户权限已更新，userId={}", id);
        return getUserPermissions(id);
    }

    // DTO
    public static class UserPermDTO {
        private int id;
        private String username;
        private List<String> permissions;

        public UserPermDTO() {}
        public UserPermDTO(int id, String username, List<String> permissions) {
            this.id = id;
            this.username = username;
            this.permissions = permissions;
        }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    }

    public static class PermUpdateDTO {
        private List<String> permissions;
        public PermUpdateDTO() {}
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    }
}