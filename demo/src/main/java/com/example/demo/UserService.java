package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService implements UserDetailsService {
    private final Map<Long, User> userMap = new HashMap<>();
    // 用于快速查找
    private final Map<String, User> usernameMap = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEsRepository userEsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usernameMap.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // 这里的密码应该是加密后的
                .roles("USER") // 默认角色
                .build();
    }

    public User register(User user) {
        if (usernameMap.containsKey(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        long id = idGen.incrementAndGet();
        user.setId(id);
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMap.put(id, user);
        usernameMap.put(user.getUsername(), user);
        return user;
    }

    // MongoDB写入
    public User addUserToMongo(User user) {
        return userRepository.save(user);
    }

    // MongoDB查询
    public List<User> listUsersFromMongo() {
        return userRepository.findAll();
    }

    // ES写入
    public User addUserToEs(User user) {
        return userEsRepository.save(user);
    }

    // ES查询
    public List<User> listUsersFromEs() {
        List<User> result = new ArrayList<>();
        userEsRepository.findAll().forEach(result::add);
        return result;
    }

    public List<User> listUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUser(Long id) {
        return userMap.get(id);
    }

    public User addUser(User user) {
        return register(user);
    }

    public User updateUser(Long id, User user) {
        if (!userMap.containsKey(id)) return null;
        user.setId(id);
        // 如果包含密码，也需要加密（这里暂简化，假设修改不改密码或已加密）
        userMap.put(id, user);
        usernameMap.put(user.getUsername(), user); // 更新索引
        return user;
    }

    public boolean deleteUser(Long id) {
        User user = userMap.remove(id);
        if (user != null) {
            usernameMap.remove(user.getUsername());
            return true;
        }
        return false;
    }
}
