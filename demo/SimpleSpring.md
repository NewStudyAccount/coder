# Spring原理简述与手写简易Spring实现

---

## 一、Spring核心原理

### 1. IOC（控制反转，Inversion of Control）
- 核心思想：对象的创建和依赖关系由容器统一管理，开发者只需声明依赖，容器负责注入。
- 主要实现方式：依赖注入（DI），包括构造器注入、Setter注入、注解注入等。

### 2. Bean生命周期
- Bean的创建、初始化、销毁由容器统一管理。
- 生命周期流程：实例化 -> 属性注入 -> 初始化方法 -> 使用 -> 销毁方法。

### 3. AOP（面向切面编程，Aspect Oriented Programming）
- 通过代理机制，在不修改业务代码的前提下实现横切关注点（如事务、日志、权限等）。
- Spring常用JDK动态代理、CGLIB字节码增强实现AOP。

### 4. 资源管理与扩展
- Spring支持配置文件、注解、JavaConfig等多种方式管理Bean。
- 支持事件、监听器、扩展点（BeanPostProcessor等）。

---

## 二、手写简易Spring实现（核心IOC/DI）

### 目标
- 支持注解注册Bean（@Component）
- 支持依赖注入（@Autowired）
- 支持通过容器获取Bean实例

### 代码结构
- SimpleApplicationContext：IOC容器，负责扫描、注册、注入Bean
- @Component/@Autowired：自定义注解
- 示例业务Bean

详见 demo/src/main/java/com/example/demo/simplespring/ 目录代码。

---

## 三、注意点

- 手写实现仅演示Spring IOC/DI核心思想，未实现AOP、Bean生命周期、作用域等完整特性。
- 真实Spring支持更丰富的配置、生命周期管理、AOP、事件、扩展点等。
- 建议结合源码和官方文档深入学习Spring原理。