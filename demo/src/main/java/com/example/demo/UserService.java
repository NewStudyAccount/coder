package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, User> userMap = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEsRepository userEsRepository;

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
