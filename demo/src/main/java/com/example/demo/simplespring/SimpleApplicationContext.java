package com.example.demo.simplespring;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 简易Spring IOC容器实现
 * 支持@Component注解注册Bean，@Autowired注入依赖
 */
public class SimpleApplicationContext {
    // Bean容器
    private Map<Class<?>, Object> beanMap = new HashMap<>();

    /**
     * 构造方法，传入所有需要管理的Bean类
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        // 1. 实例化所有@Component类
        for (Class<?> clazz : beanClasses) {
            if (clazz.isAnnotationPresent(Component.class)) {
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beanMap.put(clazz, instance);
                } catch (Exception e) {
                    throw new RuntimeException("实例化Bean失败: " + clazz, e);
                }
            }
        }
        // 2. 依赖注入
        for (Object bean : beanMap.values()) {
            injectDependencies(bean);
        }
    }

    /**
     * 依赖注入：为带@Autowired注解的字段赋值
     */
    private void injectDependencies(Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> depType = field.getType();
                Object dep = beanMap.get(depType);
                if (dep == null) {
                    throw new RuntimeException("未找到依赖Bean: " + depType);
                }
                field.setAccessible(true);
                try {
                    field.set(bean, dep);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("依赖注入失败: " + field, e);
                }
            }
        }
    }

    /**
     * 获取Bean实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz);
    }
}