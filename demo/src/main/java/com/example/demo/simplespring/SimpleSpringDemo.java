package com.example.demo.simplespring;

/**
 * 手写Spring IOC/DI演示
 */
public class SimpleSpringDemo {

    @Component
    public static class UserService {
        public String getUserName(Long id) {
            return "用户" + id;
        }
    }

    @Component
    public static class OrderService {
        // 注入UserService
        @Autowired
        private UserService userService;

        public void createOrder(Long userId, String product) {
            String user = userService.getUserName(userId);
            System.out.println("为" + user + " 创建订单，商品：" + product);
        }
    }

    public static void main(String[] args) {
        // 1. 初始化IOC容器，注册所有Bean
        SimpleApplicationContext context = new SimpleApplicationContext(UserService.class, OrderService.class);

        // 2. 获取OrderService Bean
        OrderService orderService = context.getBean(OrderService.class);

        // 3. 调用业务方法，自动注入UserService
        orderService.createOrder(1001L, "Java设计模式实战");

        // 4. 也可获取UserService
        UserService userService = context.getBean(UserService.class);
        System.out.println("单独获取用户：" + userService.getUserName(1002L));
    }
}

/*
【详细解释】
1. @Component 标记Bean，@Autowired 标记依赖字段，容器自动注入。
2. SimpleApplicationContext 构造时扫描、实例化、注入所有Bean。
3. main 方法演示IOC容器的Bean获取和依赖注入，体现Spring核心思想。
4. 真实Spring支持更多特性，如AOP、生命周期、作用域、事件等。
*/