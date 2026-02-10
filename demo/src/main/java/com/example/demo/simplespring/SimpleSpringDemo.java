package com.example.demo.simplespring;

import com.example.demo.simplespring.examples.UserController;

/**
 * Simple Spring 框架演示程序
 * 展示 IoC 容器、依赖注入、生命周期管理等核心功能
 */
public class SimpleSpringDemo {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Simple Spring 框架演示");
        System.out.println("========================================\n");
        
        // 可以通过系统属性设置配置值
        System.setProperty("app.name", "用户管理系统");
        System.setProperty("app.version", "2.0.0");
        
        System.out.println(">>> 步骤 1: 初始化 IoC 容器");
        System.out.println("扫描包路径: com.example.demo.simplespring.examples\n");
        
        // 创建 Spring 容器，自动扫描组件、实例化 Bean、注入依赖
        SimpleSpringContext context = new SimpleSpringContext(
            "com.example.demo.simplespring.examples"
        );
        
        System.out.println("\n>>> 步骤 2: 查看容器中的所有 Bean");
        System.out.println("Bean 列表: " + context.getBeanNames());
        
        System.out.println("\n>>> 步骤 3: 从容器中获取 Bean");
        UserController controller = context.getBean(UserController.class);
        System.out.println("获取到 UserController Bean: " + controller.getClass().getName());
        
        System.out.println("\n>>> 步骤 4: 测试应用功能");
        
        // 测试获取应用信息（验证 @Value 注入）
        controller.handleAppInfo();
        
        // 测试获取用户列表（验证初始数据）
        controller.handleListUsers();
        
        // 测试获取单个用户
        controller.handleGetUser(1L);
        controller.handleGetUser(999L);
        
        // 测试创建新用户
        controller.handleCreateUser(4L, "赵六");
        controller.handleCreateUser(5L, "孙七");
        
        // 验证创建结果
        controller.handleListUsers();
        
        // 测试异常处理
        controller.handleCreateUser(6L, "");
        
        System.out.println("\n>>> 步骤 5: 关闭容器");
        System.out.println("执行 Bean 销毁方法...\n");
        context.close();
        
        System.out.println("\n========================================");
        System.out.println("   演示完成");
        System.out.println("========================================");
        
        printFrameworkSummary();
    }
    
    private static void printFrameworkSummary() {
        System.out.println("\n【Simple Spring 框架功能总结】");
        System.out.println("1. ✓ IoC 容器 - 自动管理对象生命周期");
        System.out.println("2. ✓ 组件扫描 - @Component 自动发现和注册");
        System.out.println("3. ✓ 依赖注入 - @Autowired 自动装配");
        System.out.println("4. ✓ 配置注入 - @Value 支持占位符和默认值");
        System.out.println("5. ✓ 初始化回调 - @PostConstruct 在依赖注入后执行");
        System.out.println("6. ✓ 销毁回调 - @PreDestroy 在容器关闭时执行");
        System.out.println("7. ✓ 单例模式 - 所有 Bean 默认单例");
        System.out.println("8. ✓ 类型安全 - 支持按类型和名称获取 Bean");
        System.out.println("\n核心设计模式:");
        System.out.println("  - 工厂模式: IoC 容器作为 Bean 工厂");
        System.out.println("  - 单例模式: Bean 的单例管理");
        System.out.println("  - 依赖注入: 控制反转的具体实现");
        System.out.println("  - 模板方法: 生命周期回调");
    }
}
