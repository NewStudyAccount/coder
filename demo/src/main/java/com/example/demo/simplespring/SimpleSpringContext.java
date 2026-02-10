package com.example.demo.simplespring;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简化版 Spring IoC 容器实现
 * 支持组件扫描、依赖注入、单例模式
 */
public class SimpleSpringContext {
    
    // Bean 容器 - 存储所有单例 Bean
    private final Map<String, Object> singletonBeans = new ConcurrentHashMap<>();
    
    // Bean 定义 - 存储 Bean 的类信息
    private final Map<String, Class<?>> beanDefinitions = new ConcurrentHashMap<>();
    
    // Bean 名称映射 - 类型到名称的映射
    private final Map<Class<?>, String> typeToBeanName = new ConcurrentHashMap<>();
    
    // 扫描的基础包路径
    private final String basePackage;
    
    /**
     * 构造函数 - 初始化容器并扫描组件
     * 
     * @param basePackage 要扫描的基础包路径
     */
    public SimpleSpringContext(String basePackage) {
        this.basePackage = basePackage;
        // 扫描组件
        scanComponents();
        // 实例化所有 Bean
        instantiateBeans();
        // 执行依赖注入
        injectDependencies();
        // 执行初始化方法
        invokeInitMethods();
    }
    
    /**
     * 扫描指定包下的所有组件
     */
    private void scanComponents() {
        try {
            // 将包名转换为文件路径
            String packagePath = basePackage.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL packageUrl = classLoader.getResource(packagePath);
            
            if (packageUrl == null) {
                System.out.println("警告: 无法找到包路径 " + basePackage);
                return;
            }
            
            File packageDir = new File(packageUrl.getFile());
            
            // 递归扫描所有类文件
            scanDirectory(packageDir, basePackage);
            
            System.out.println("组件扫描完成，共发现 " + beanDefinitions.size() + " 个 Bean");
            
        } catch (Exception e) {
            throw new RuntimeException("组件扫描失败", e);
        }
    }
    
    /**
     * 递归扫描目录中的类文件
     */
    private void scanDirectory(File dir, String packageName) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                // 处理类文件
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    
                    // 检查是否有 @Component 注解
                    if (clazz.isAnnotationPresent(Component.class)) {
                        Component component = clazz.getAnnotation(Component.class);
                        String beanName = component.value();
                        
                        // 如果没有指定名称，使用类名首字母小写
                        if (beanName.isEmpty()) {
                            beanName = uncapitalize(clazz.getSimpleName());
                        }
                        
                        // 注册 Bean 定义
                        beanDefinitions.put(beanName, clazz);
                        typeToBeanName.put(clazz, beanName);
                        
                        System.out.println("发现组件: " + beanName + " -> " + clazz.getName());
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("无法加载类: " + className);
                }
            }
        }
    }
    
    /**
     * 实例化所有 Bean
     */
    private void instantiateBeans() {
        for (Map.Entry<String, Class<?>> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            Class<?> beanClass = entry.getValue();
            
            try {
                // 使用无参构造函数创建实例
                Constructor<?> constructor = beanClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object instance = constructor.newInstance();
                
                // 存储到单例容器
                singletonBeans.put(beanName, instance);
                
                System.out.println("实例化 Bean: " + beanName);
                
            } catch (Exception e) {
                throw new RuntimeException("实例化 Bean 失败: " + beanName, e);
            }
        }
    }
    
    /**
     * 执行依赖注入
     */
    private void injectDependencies() {
        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            
            // 注入字段
            injectFields(bean, beanName);
            
            // 注入属性值
            injectValues(bean, beanName);
        }
    }
    
    /**
     * 注入字段依赖 (@Autowired)
     */
    private void injectFields(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
                    // 根据字段类型查找 Bean
                    Class<?> fieldType = field.getType();
                    String targetBeanName = typeToBeanName.get(fieldType);
                    
                    if (targetBeanName == null) {
                        throw new RuntimeException("无法找到类型为 " + fieldType.getName() + " 的 Bean");
                    }
                    
                    Object dependency = singletonBeans.get(targetBeanName);
                    
                    // 设置字段值
                    field.setAccessible(true);
                    field.set(bean, dependency);
                    
                    System.out.println("注入依赖: " + beanName + "." + field.getName() + " -> " + targetBeanName);
                    
                } catch (Exception e) {
                    throw new RuntimeException("依赖注入失败: " + beanName + "." + field.getName(), e);
                }
            }
        }
    }
    
    /**
     * 注入配置值 (@Value)
     */
    private void injectValues(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(Value.class)) {
                try {
                    Value valueAnnotation = field.getAnnotation(Value.class);
                    String value = valueAnnotation.value();
                    
                    // 简单处理 ${} 占位符
                    if (value.startsWith("${") && value.endsWith("}")) {
                        String propertyName = value.substring(2, value.length() - 1);
                        String[] parts = propertyName.split(":");
                        String key = parts[0];
                        String defaultValue = parts.length > 1 ? parts[1] : null;
                        
                        // 从系统属性或环境变量获取值
                        value = System.getProperty(key, System.getenv(key));
                        if (value == null) {
                            value = defaultValue;
                        }
                    }
                    
                    if (value != null) {
                        // 类型转换
                        Object convertedValue = convertValue(value, field.getType());
                        
                        // 设置字段值
                        field.setAccessible(true);
                        field.set(bean, convertedValue);
                        
                        System.out.println("注入配置值: " + beanName + "." + field.getName() + " = " + value);
                    }
                    
                } catch (Exception e) {
                    throw new RuntimeException("配置值注入失败: " + beanName + "." + field.getName(), e);
                }
            }
        }
    }
    
    /**
     * 执行初始化方法 (@PostConstruct)
     */
    private void invokeInitMethods() {
        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            
            Class<?> clazz = bean.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean);
                        
                        System.out.println("执行初始化方法: " + beanName + "." + method.getName() + "()");
                        
                    } catch (Exception e) {
                        throw new RuntimeException("初始化方法执行失败: " + beanName + "." + method.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * 根据名称获取 Bean
     * 
     * @param name Bean 名称
     * @return Bean 实例
     */
    public Object getBean(String name) {
        Object bean = singletonBeans.get(name);
        if (bean == null) {
            throw new RuntimeException("Bean 不存在: " + name);
        }
        return bean;
    }
    
    /**
     * 根据类型获取 Bean
     * 
     * @param type Bean 类型
     * @return Bean 实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        String beanName = typeToBeanName.get(type);
        if (beanName == null) {
            throw new RuntimeException("Bean 不存在: " + type.getName());
        }
        return (T) getBean(beanName);
    }
    
    /**
     * 根据名称和类型获取 Bean
     * 
     * @param name Bean 名称
     * @param type Bean 类型
     * @return Bean 实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> type) {
        Object bean = getBean(name);
        if (!type.isInstance(bean)) {
            throw new RuntimeException("Bean 类型不匹配: " + name + " 不是 " + type.getName());
        }
        return (T) bean;
    }
    
    /**
     * 获取所有 Bean 名称
     * 
     * @return Bean 名称集合
     */
    public Set<String> getBeanNames() {
        return new HashSet<>(singletonBeans.keySet());
    }
    
    /**
     * 检查是否包含指定名称的 Bean
     * 
     * @param name Bean 名称
     * @return 是否包含
     */
    public boolean containsBean(String name) {
        return singletonBeans.containsKey(name);
    }
    
    /**
     * 获取 Bean 的类型
     * 
     * @param name Bean 名称
     * @return Bean 类型
     */
    public Class<?> getType(String name) {
        return beanDefinitions.get(name);
    }
    
    /**
     * 关闭容器，执行销毁方法 (@PreDestroy)
     */
    public void close() {
        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            
            Class<?> clazz = bean.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            
            for (Method method : methods) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean);
                        
                        System.out.println("执行销毁方法: " + beanName + "." + method.getName() + "()");
                        
                    } catch (Exception e) {
                        System.err.println("销毁方法执行失败: " + beanName + "." + method.getName());
                    }
                }
            }
        }
        
        singletonBeans.clear();
        beanDefinitions.clear();
        typeToBeanName.clear();
    }
    
    /**
     * 将字符串首字母小写
     */
    private String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 值类型转换
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else {
            throw new RuntimeException("不支持的类型转换: " + targetType.getName());
        }
    }
}
