package com.example.demo.simplespring.autoconfig;

import com.example.demo.simplespring.Autowired;
import com.example.demo.simplespring.Component;
import com.example.demo.simplespring.SimpleApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 简易“自动配置”演示
 * 目标：模拟 Spring Boot 自动配置的核心思想——按条件注册 Bean
 *
 * 内容包括：
 * 1) 条件注解（OnProperty、OnMissingBean）
 * 2) 自动配置类（RedisAutoConfiguration、KafkaAutoConfiguration）
 * 3) 注册器（AutoConfigRegistrar）按配置与容器现状，自动注册 Bean
 * 4) 演示入口（main）
 *
 * 注意：为教学简化版，未实现完整的类路径扫描、环境绑定、优先级、禁用等复杂特性。
 */
public class AutoConfigurationDemo {

    /* ==========================
       条件注解（简化版实现）
       ========================== */

    /**
     * 条件：当配置项为指定值时生效
     */
    @interface ConditionalOnProperty {
        String key();
        String value();
    }

    /**
     * 条件：当容器中缺少某类型 Bean 时生效
     */
    @interface ConditionalOnMissingBean {
        Class<?> value();
    }

    /* ==========================
       自动配置类（示例）
       ========================== */

    /**
     * 模拟的 Redis 客户端（示例 Bean）
     */
    @Component
    public static class RedisClient {
        public String ping() {
            return "PONG";
        }
    }

    /**
     * 自动配置：当 redis.enabled=true 且容器中没有 RedisClient 时，注册 RedisClient
     */
    @ConditionalOnProperty(key = "redis.enabled", value = "true")
    @ConditionalOnMissingBean(RedisClient.class)
    public static class RedisAutoConfiguration {
        public static void register(SimpleApplicationContext context) {
            // 注册 RedisClient Bean
            try {
                RedisClient client = RedisClient.class.getDeclaredConstructor().newInstance();
                // 直接放入容器（简化：访问容器内部存储的方式，在此示例中通过扩展 registerBean 实现更合理）
                AutoConfigRegistrar.registerBean(context, RedisClient.class, client);
                System.out.println("[AutoConfig] RedisClient 已自动注册");
            } catch (Exception e) {
                throw new RuntimeException("RedisClient 注册失败", e);
            }
        }
    }

    /**
     * 模拟的 Kafka 客户端（示例 Bean）
     */
    @Component
    public static class KafkaClient {
        public void send(String topic, String msg) {
            System.out.println("Kafka[" + topic + "]: " + msg);
        }
    }

    /**
     * 自动配置：当 kafka.enabled=true 且容器中没有 KafkaClient 时，注册 KafkaClient
     */
    @ConditionalOnProperty(key = "kafka.enabled", value = "true")
    @ConditionalOnMissingBean(KafkaClient.class)
    public static class KafkaAutoConfiguration {
        public static void register(SimpleApplicationContext context) {
            try {
                KafkaClient client = KafkaClient.class.getDeclaredConstructor().newInstance();
                AutoConfigRegistrar.registerBean(context, KafkaClient.class, client);
                System.out.println("[AutoConfig] KafkaClient 已自动注册");
            } catch (Exception e) {
                throw new RuntimeException("KafkaClient 注册失败", e);
            }
        }
    }

    /* ==========================
       自动配置注册器
       ========================== */

    /**
     * 自动配置注册器：根据配置和容器现状，评估条件并注册 Bean
     */
    public static class AutoConfigRegistrar {
        private final Map<String, String> properties = new HashMap<>();
        private final SimpleApplicationContext context;

        public AutoConfigRegistrar(SimpleApplicationContext context, Map<String, String> props) {
            this.context = context;
            if (props != null) {
                this.properties.putAll(props);
            }
        }

        /**
         * 统一注册入口：遍历候选自动配置类，评估条件后注册
         */
        public void registerAll() {
            // 候选自动配置类列表（手工维护，模拟 spring.factories）
            Class<?>[] candidates = new Class<?>[]{
                    RedisAutoConfiguration.class,
                    KafkaAutoConfiguration.class
            };
            for (Class<?> autoConfigClass : candidates) {
                if (matchConditions(autoConfigClass)) {
                    // 通过反射调用静态 register(SimpleApplicationContext) 方法
                    try {
                        autoConfigClass
                                .getMethod("register", SimpleApplicationContext.class)
                                .invoke(null, context);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(autoConfigClass.getSimpleName() + " 缺少 register(SimpleApplicationContext) 方法", e);
                    } catch (Exception e) {
                        throw new RuntimeException("自动配置执行失败: " + autoConfigClass.getSimpleName(), e);
                    }
                } else {
                    System.out.println("[AutoConfig] 条件不匹配，跳过: " + autoConfigClass.getSimpleName());
                }
            }
        }

        /**
         * 评估类上的条件注解（简化版）
         */
        private boolean matchConditions(Class<?> autoConfigClass) {
            // 检查 ConditionalOnProperty
            ConditionalOnProperty onProp = autoConfigClass.getAnnotation(ConditionalOnProperty.class);
            if (onProp != null) {
                String actual = properties.getOrDefault(onProp.key(), "false");
                if (!onProp.value().equalsIgnoreCase(actual)) {
                    return false;
                }
            }
            // 检查 ConditionalOnMissingBean
            ConditionalOnMissingBean onMissing = autoConfigClass.getAnnotation(ConditionalOnMissingBean.class);
            if (onMissing != null) {
                Class<?> beanType = onMissing.value();
                Object bean = context.getBean(beanType);
                if (bean != null) {
                    return false; // 容器已有该 Bean，则不触发自动配置
                }
            }
            return true;
        }

        /**
         * 简化的 Bean 注册方法（通过上下文注册新的 Bean）
         * 说明：由于 SimpleApplicationContext 未暴露注册方法，这里提供一个静态辅助。
         * 更合理的做法是为 SimpleApplicationContext 增加 registerBean API。
         */
        public static <T> void registerBean(SimpleApplicationContext context, Class<T> type, T instance) {
            // 由于 SimpleApplicationContext 的 beanMap 不可直接访问，这里退而求其次：
            // 通过“桥接”方式：先获取已存在 Bean，若不存在，则利用反射将实例注入使用者（本示例仅用于演示自动注册效果）
            // 为保证示例可运行，我们改用“使用者按需获取”的测试方式。
            Holder.add(type, instance);
        }
    }

    /**
     * 简易 Holder：模拟容器新增 Bean 的存储（演示用途）
     * 实际应修改 SimpleApplicationContext 支持 registerBean。
     */
    public static class Holder {
        private static final Map<Class<?>, Object> extraBeans = new HashMap<>();
        public static <T> void add(Class<T> type, T instance) {
            extraBeans.put(type, instance);
        }
        @SuppressWarnings("unchecked")
        public static <T> T get(Class<T> type) {
            return (T) extraBeans.get(type);
        }
    }

    /* ==========================
       演示入口
       ========================== */

    @Component
    public static class BusinessService {
        // 依赖可由自动配置提供（RedisClient、KafkaClient）
        @Autowired
        private RedisClient redisClient; // 若开启 redis.enabled=true 将自动可用

        public void doWork() {
            if (redisClient != null) {
                System.out.println("Redis PING: " + redisClient.ping());
            } else {
                System.out.println("RedisClient 未启用");
            }
        }
    }

    public static void main(String[] args) {
        // 1. 初始化用户自定义 Bean 容器
        SimpleApplicationContext context = new SimpleApplicationContext(BusinessService.class);

        // 2. 模拟配置（类似 application.properties）
        Map<String, String> props = new HashMap<>();
        props.put("redis.enabled", "true");
        props.put("kafka.enabled", "false");

        // 3. 自动配置注册器：根据配置与容器状态自动注册 Bean
        AutoConfigRegistrar registrar = new AutoConfigRegistrar(context, props);
        registrar.registerAll();

        // 4. 将自动注册的 Bean 注入到业务 Bean（教学简化：手动二次注入）
        // 实际应由容器维护新增的 Bean 并进行依赖注入，这里演示思路：
        BusinessService biz = context.getBean(BusinessService.class);
        // 将 Holder 中的自动注册 Bean 注入到业务 Bean
        RedisClient redis = Holder.get(RedisClient.class);
        if (redis != null) {
            try {
                BusinessService.class.getDeclaredField("redisClient").setAccessible(true);
                BusinessService.class.getDeclaredField("redisClient").set(biz, redis);
            } catch (Exception e) {
                throw new RuntimeException("自动注入 RedisClient 失败", e);
            }
        }

        // 5. 业务调用，验证自动配置效果
        biz.doWork();
    }
}

/*
【详细说明】
1. 条件注解（ConditionalOnProperty/ConditionalOnMissingBean）用于声明自动配置触发条件：
   - ConditionalOnProperty(key, value)：当配置项为指定值时生效；
   - ConditionalOnMissingBean(type)：当容器中没有某类型 Bean 时生效。
2. AutoConfigRegistrar 负责遍历所有候选自动配置类，评估条件并注册对应 Bean。
3. RedisAutoConfiguration/KafkaAutoConfiguration 展示“按需注册”的思想：
   - 当 redis.enabled=true 且没有 RedisClient Bean，自动注册之。
4. 为简化演示，SimpleApplicationContext 未提供 registerBean API，
   示例通过 Holder 暂存自动注册的 Bean，并在演示入口中手动注入到业务 Bean。
   在真实实现中，应扩展 SimpleApplicationContext：
   - 增加 registerBean(Class, Object)
   - 在自动配置注册完成后，统一执行一次依赖注入（遍历所有 Bean 字段）
5. 与 Spring Boot 的自动配置相比，本示例仅演示核心思想：
   - Spring Boot 使用 spring.factories/AutoConfiguration.imports 声明候选类；
   - 使用 ConditionEvaluator 评估复杂条件；
   - 通过 Environment/PropertySource/Binder 进行配置绑定；
   - 完整生命周期由 BeanFactory/PostProcessor 管理。
6. 可扩展方向：
   - 增加 @Bean 方法级注册；
   - 支持优先级、禁用（如 @EnableAutoConfiguration 排除）；
   - 实现完整依赖注入流程与 Bean 生命周期。
*/