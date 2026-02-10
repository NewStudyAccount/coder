package com.example.demo.simplespring.examples;

import com.example.demo.simplespring.Autowired;
import com.example.demo.simplespring.Component;
import com.example.demo.simplespring.PostConstruct;

/**
 * 用户控制器示例 - 表示层
 * 演示多层依赖注入
 */
@Component
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void init() {
        System.out.println("UserController 初始化...");
    }
    
    public void handleGetUser(Long id) {
        System.out.println("\n=== 处理获取用户请求 ===");
        System.out.println("请求参数: userId = " + id);
        String result = userService.getUserById(id);
        System.out.println("响应结果: " + result);
    }
    
    public void handleCreateUser(Long id, String name) {
        System.out.println("\n=== 处理创建用户请求 ===");
        System.out.println("请求参数: userId = " + id + ", userName = " + name);
        try {
            userService.createUser(id, name);
            System.out.println("响应结果: 用户创建成功");
        } catch (Exception e) {
            System.out.println("响应结果: 创建失败 - " + e.getMessage());
        }
    }
    
    public void handleListUsers() {
        System.out.println("\n=== 处理获取用户列表请求 ===");
        String result = userService.getAllUsers();
        System.out.println("响应结果:\n" + result);
    }
    
    public void handleAppInfo() {
        System.out.println("\n=== 处理获取应用信息请求 ===");
        String result = userService.getAppInfo();
        System.out.println("响应结果: " + result);
    }
}
