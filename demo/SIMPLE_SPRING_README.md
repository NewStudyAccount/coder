# Simple Spring 框架实现文档

## 📋 概述

这是一个简化版的 Spring 框架实现，演示了 Spring 核心功能的工作原理，包括 IoC 容器、依赖注入、组件扫描和生命周期管理。

## 🎯 核心功能

### 1. IoC 容器 (Inversion of Control)
- 自动管理对象的创建和生命周期
- 单例模式管理所有 Bean
- 支持按名称和类型获取 Bean

### 2. 依赖注入 (Dependency Injection)
- `@Autowired` - 自动装配依赖
- `@Value` - 注入配置值
- 支持字段注入

### 3. 组件扫描
- `@Component` - 标记组件类
- 自动扫描指定包路径
- 递归扫描子包

### 4. 生命周期管理
- `@PostConstruct` - 初始化回调
- `@PreDestroy` - 销毁回调

## 📁 项目结构

```
demo/src/main/java/com/example/demo/simplespring/
├── SimpleSpringContext.java      # IoC 容器核心实现
├── Component.java                 # @Component 注解
├── Autowired.java                 # @Autowired 注解
├── Value.java                     # @Value 注解
├── PostConstruct.java             # @PostConstruct 注解
├── PreDestroy.java                # @PreDestroy 注解
├── SimpleSpringDemo.java          # 演示主程序
└── examples/
    ├── UserRepository.java        # 数据访问层示例
    ├── UserService.java           # 业务逻辑层示例
    └── UserController.java        # 表示层示例
```

## 🚀 快速开始

### 1. 创建组件类

```java
package com.example.demo.simplespring.examples;

import com.example.demo.simplespring.Component;
import com.example.demo.simplespring.Autowired;
import com.example.demo.simplespring.Value;
import com.example.demo.simplespring.PostConstruct;
import com.example.demo.simplespring.PreDestroy;

@Component  // 标记为 Spring 组件
public class UserService {
    
    @Autowired  // 自动注入依赖
    private UserRepository userRepository;
    
    @Value("${app.name:DefaultApp}")  // 注入配置值，支持默认值
    private String appName;
    
    @PostConstruct  // 初始化方法
    public void init() {
        System.out.println("UserService 初始化完成");
    }
    
    @PreDestroy  // 销毁方法
    public void cleanup() {
        System.out.println("UserService 清理资源");
    }
    
    public void doSomething() {
        // 业务逻辑
    }
}
```

### 2. 初始化容器

```java
package com.example.demo.simplespring;

public class Application {
    public static void main(String[] args) {
        // 设置配置值（可选）
        System.setProperty("app.name", "我的应用");
        
        // 创建 IoC 容器，自动扫描组件
        SimpleSpringContext context = new SimpleSpringContext(
            "com.example.demo.simplespring.examples"
        );
        
        // 从容器获取 Bean
        UserService userService = context.getBean(UserService.class);
        
        // 使用 Bean
        userService.doSomething();
        
        // 关闭容器
        context.close();
    }
}
```

## 📚 详细说明

### IoC 容器 API

#### 构造函数
```java
SimpleSpringContext context = new SimpleSpringContext(String basePackage);
```
- `basePackage`: 要扫描的基础包路径
- 自动执行：组件扫描 → Bean 实例化 → 依赖注入 → 初始化方法调用

#### 获取 Bean
```java
// 按类型获取
UserService service = context.getBean(UserService.class);

// 按名称获取
Object bean = context.getBean("userService");

// 按名称和类型获取
UserService service = context.getBean("userService", UserService.class);
```

#### 容器管理
```java
// 获取所有 Bean 名称
Set<String> names = context.getBeanNames();

// 检查是否包含指定 Bean
boolean exists = context.containsBean("userService");

// 获取 Bean 的类型
Class<?> type = context.getType("userService");

// 关闭容器（触发 @PreDestroy 方法）
context.close();
```

### 注解说明

#### @Component
标记一个类为 Spring 组件，容器会自动扫描并注册。

```java
@Component  // 默认 Bean 名称为类名首字母小写
public class UserService { }

@Component("customName")  // 指定 Bean 名称
public class UserService { }
```

#### @Autowired
自动装配依赖，按类型注入。

```java
@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;  // 自动注入
}
```

#### @Value
注入配置值，支持占位符和默认值。

```java
@Component
public class UserService {
    // 直接值
    @Value("Hello")
    private String greeting;
    
    // 从系统属性获取
    @Value("${app.name}")
    private String appName;
    
    // 带默认值
    @Value("${app.version:1.0.0}")
    private String version;
}
```

配置值优先级：
1. 系统属性：`System.setProperty("app.name", "value")`
2. 环境变量：`System.getenv("app.name")`
3. 默认值：`${app.name:defaultValue}`

#### @PostConstruct
标记初始化方法，在依赖注入完成后执行。

```java
@Component
public class UserService {
    @Autowired
    private UserRepository repository;
    
    @PostConstruct
    public void init() {
        // 依赖已注入，可以安全使用
        System.out.println("初始化完成");
    }
}
```

#### @PreDestroy
标记销毁方法，在容器关闭时执行。

```java
@Component
public class UserService {
    @PreDestroy
    public void cleanup() {
        // 清理资源
        System.out.println("清理资源");
    }
}
```

## 🔄 Bean 生命周期

```
1. 组件扫描
   ↓
2. Bean 实例化（调用无参构造函数）
   ↓
3. 依赖注入（@Autowired）
   ↓
4. 配置值注入（@Value）
   ↓
5. 初始化方法（@PostConstruct）
   ↓
6. Bean 就绪，可以使用
   ↓
7. 容器关闭
   ↓
8. 销毁方法（@PreDestroy）
```

## 🎨 设计模式

### 1. 工厂模式
IoC 容器作为 Bean 工厂，负责创建和管理对象。

### 2. 单例模式
所有 Bean 默认为单例，容器中只有一个实例。

### 3. 依赖注入模式
通过注解实现控制反转，对象不再自己创建依赖。

### 4. 模板方法模式
生命周期回调方法（@PostConstruct、@PreDestroy）。

## 🏗️ 架构设计

### 核心类: SimpleSpringContext

```java
public class SimpleSpringContext {
    // Bean 容器
    private Map<String, Object> singletonBeans;
    
    // Bean 定义
    private Map<String, Class<?>> beanDefinitions;
    
    // 类型到名称的映射
    private Map<Class<?>, String> typeToBeanName;
    
    // 构造函数
    public SimpleSpringContext(String basePackage) {
        scanComponents();      // 1. 扫描组件
        instantiateBeans();    // 2. 实例化 Bean
        injectDependencies();  // 3. 依赖注入
        invokeInitMethods();   // 4. 初始化方法
    }
}
```

### 扫描流程
1. 将包名转换为文件路径
2. 递归扫描目录中的 .class 文件
3. 检查类是否有 @Component 注解
4. 注册 Bean 定义

### 依赖注入流程
1. 遍历所有 Bean
2. 检查字段是否有 @Autowired 注解
3. 根据字段类型查找对应的 Bean
4. 使用反射设置字段值

## 🧪 运行演示

### 编译项目
```bash
cd demo
mvn clean compile
```

### 运行演示程序
```bash
mvn exec:java -Dexec.mainClass="com.example.demo.simplespring.SimpleSpringDemo"
```

或者在 IDE 中直接运行 `SimpleSpringDemo.java` 的 main 方法。

### 预期输出

```
========================================
   Simple Spring 框架演示
========================================

>>> 步骤 1: 初始化 IoC 容器
扫描包路径: com.example.demo.simplespring.examples

发现组件: userRepository -> com.example.demo.simplespring.examples.UserRepository
发现组件: userService -> com.example.demo.simplespring.examples.UserService
发现组件: userController -> com.example.demo.simplespring.examples.UserController
组件扫描完成，共发现 3 个 Bean

实例化 Bean: userRepository
实例化 Bean: userService
实例化 Bean: userController

注入依赖: userService.userRepository -> userRepository
注入依赖: userController.userService -> userService
注入配置值: userService.appName = 用户管理系统
注入配置值: userService.appVersion = 2.0.0

UserRepository 初始化...
UserService 初始化...
应用名称: 用户管理系统
应用版本: 2.0.0
UserController 初始化...

>>> 步骤 2: 查看容器中的所有 Bean
Bean 列表: [userRepository, userService, userController]

>>> 步骤 3: 从容器中获取 Bean
获取到 UserController Bean: com.example.demo.simplespring.examples.UserController

>>> 步骤 4: 测试应用功能

=== 处理获取应用信息请求 ===
响应结果: 应用: 用户管理系统 v2.0.0

=== 处理获取用户列表请求 ===
响应结果:
所有用户:
  ID: 1, 姓名: 张三
  ID: 2, 姓名: 李四
  ID: 3, 姓名: 王五

...

>>> 步骤 5: 关闭容器
执行 Bean 销毁方法...

UserRepository 清理资源...
执行销毁方法: userRepository.cleanup()

========================================
   演示完成
========================================
```

## 🔍 实现原理

### 反射机制
使用 Java 反射 API 实现动态功能：
- `Class.forName()` - 加载类
- `Constructor.newInstance()` - 创建实例
- `Field.set()` - 设置字段值
- `Method.invoke()` - 调用方法

### 注解处理
使用运行时注解（`@Retention(RetentionPolicy.RUNTIME)`）：
- `isAnnotationPresent()` - 检查是否有注解
- `getAnnotation()` - 获取注解信息

### 类型安全
使用泛型和类型检查确保类型安全：
```java
@SuppressWarnings("unchecked")
public <T> T getBean(Class<T> type) {
    // 类型安全的 Bean 获取
}
```

## 🆚 与真实 Spring 的对比

### 已实现的功能
- ✅ IoC 容器
- ✅ 依赖注入（字段注入）
- ✅ 组件扫描
- ✅ 生命周期管理
- ✅ 配置值注入
- ✅ 单例模式

### 未实现的功能
- ❌ 构造器注入和 Setter 注入
- ❌ Bean 作用域（原型、请求、会话等）
- ❌ AOP（面向切面编程）
- ❌ 事件机制
- ❌ 条件装配
- ❌ 配置类（@Configuration）
- ❌ Bean 后置处理器
- ❌ 循环依赖处理
- ❌ 懒加载

## 💡 扩展建议

如果想扩展此框架，可以考虑添加：

1. **构造器注入**：支持通过构造函数注入依赖
2. **循环依赖检测**：检测并处理 Bean 之间的循环依赖
3. **Bean 作用域**：支持原型模式等其他作用域
4. **条件装配**：根据条件决定是否创建 Bean
5. **配置类支持**：支持 @Configuration 和 @Bean 注解
6. **AOP 支持**：实现简单的代理机制
7. **属性文件支持**：从 properties 或 yaml 文件读取配置

## 📝 最佳实践

1. **组件职责单一**：每个组件只负责一个功能
2. **依赖注入优于手动创建**：让容器管理对象生命周期
3. **使用接口**：面向接口编程，提高可测试性
4. **合理使用生命周期方法**：初始化和清理资源
5. **避免循环依赖**：设计时避免 A 依赖 B，B 又依赖 A

## 🔧 故障排查

### Bean 不存在
```
RuntimeException: Bean 不存在: userService
```
**解决方案**：
- 检查类是否有 @Component 注解
- 确认包路径是否正确
- 查看组件扫描日志

### 依赖注入失败
```
RuntimeException: 无法找到类型为 XXX 的 Bean
```
**解决方案**：
- 确认依赖的类也有 @Component 注解
- 检查是否在同一个扫描路径下
- 确认字段类型是否正确

### 配置值为 null
```
注入配置值: userService.appName = null
```
**解决方案**：
- 使用 System.setProperty() 设置属性
- 或者在 @Value 中提供默认值

## 📖 参考资源

- [Spring Framework 官方文档](https://spring.io/projects/spring-framework)
- [控制反转 (IoC) 设计模式](https://en.wikipedia.org/wiki/Inversion_of_control)
- [依赖注入 (DI) 设计模式](https://en.wikipedia.org/wiki/Dependency_injection)
- Java 反射 API 文档

## 📄 许可证

本项目仅用于学习和演示目的。

---

**注意**：这是一个教学示例，不建议在生产环境中使用。如需完整的企业级功能，请使用官方的 Spring Framework。
