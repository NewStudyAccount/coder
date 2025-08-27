package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, User> userMap = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    public List<User> listUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUser(Long id) {
        return userMap.get(id);
    }

    public User addUser(User user) {
        long id = idGen.incrementAndGet();
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    public User updateUser(Long id, User user) {
        if (!userMap.containsKey(id)) return null;
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    public boolean deleteUser(Long id) {
        return userMap.remove(id) != null;
    }
}