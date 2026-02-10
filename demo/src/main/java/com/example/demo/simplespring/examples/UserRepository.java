package com.example.demo.simplespring.examples;

import com.example.demo.simplespring.Component;
import com.example.demo.simplespring.PostConstruct;
import com.example.demo.simplespring.PreDestroy;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户仓储示例 - 数据访问层
 */
@Component
public class UserRepository {
    
    private Map<Long, String> users;
    
    @PostConstruct
    public void init() {
        System.out.println("UserRepository 初始化...");
        users = new HashMap<>();
        users.put(1L, "张三");
        users.put(2L, "李四");
        users.put(3L, "王五");
    }
    
    public String findById(Long id) {
        return users.get(id);
    }
    
    public void save(Long id, String name) {
        users.put(id, name);
        System.out.println("保存用户: " + id + " -> " + name);
    }
    
    public Map<Long, String> findAll() {
        return new HashMap<>(users);
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("UserRepository 清理资源...");
        users.clear();
    }
}
