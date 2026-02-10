package com.example.demo.simplespring.examples;

import com.example.demo.simplespring.Autowired;
import com.example.demo.simplespring.Component;
import com.example.demo.simplespring.PostConstruct;
import com.example.demo.simplespring.Value;

import java.util.Map;

/**
 * 用户服务示例 - 业务逻辑层
 * 演示依赖注入和配置值注入
 */
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.name:Simple Spring Demo}")
    private String appName;
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @PostConstruct
    public void init() {
        System.out.println("UserService 初始化...");
        System.out.println("应用名称: " + appName);
        System.out.println("应用版本: " + appVersion);
    }
    
    public String getUserById(Long id) {
        String user = userRepository.findById(id);
        if (user == null) {
            return "用户不存在: " + id;
        }
        return "用户信息: " + user;
    }
    
    public void createUser(Long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        userRepository.save(id, name);
    }
    
    public String getAllUsers() {
        Map<Long, String> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder("所有用户:\n");
        users.forEach((id, name) -> 
            sb.append("  ID: ").append(id).append(", 姓名: ").append(name).append("\n")
        );
        return sb.toString();
    }
    
    public String getAppInfo() {
        return String.format("应用: %s v%s", appName, appVersion);
    }
}
