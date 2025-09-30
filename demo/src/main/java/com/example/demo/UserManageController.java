package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/users")
public class UserManageController {

    private final List<UserDTO> users = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idGen = new AtomicInteger(1);

    public UserManageController() {
        users.add(new UserDTO(idGen.getAndIncrement(), "张三", "zhangsan@example.com"));
        users.add(new UserDTO(idGen.getAndIncrement(), "李四", "lisi@example.com"));
        users.add(new UserDTO(idGen.getAndIncrement(), "王五", "wangwu@example.com"));
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return users;
    }

    @PostMapping
    public UserDTO addUser(@RequestBody UserDTO user) {
        int id = idGen.getAndIncrement();
        UserDTO newUser = new UserDTO(id, user.getUsername(), user.getEmail());
        users.add(newUser);
        return newUser;
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable int id, @RequestBody UserDTO user) {
        for (UserDTO u : users) {
            if (u.getId() == id) {
                u.setUsername(user.getUsername());
                u.setEmail(user.getEmail());
                return u;
            }
        }
        throw new NoSuchElementException("用户不存在");
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        users.removeIf(u -> u.getId() == id);
    }

    // DTO 内部类
    public static class UserDTO {
        private int id;
        private String username;
        private String email;

        public UserDTO() {}

        public UserDTO(int id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}