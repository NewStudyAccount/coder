package com.example.demo.simplespring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 值注入注解 - 用于注入配置值
 * 类似于 Spring 的 @Value 注解
 * 
 * 支持的格式：
 * - 直接值: @Value("hello")
 * - 占位符: @Value("${app.name}")
 * - 带默认值的占位符: @Value("${app.name:MyApp}")
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    
    /**
     * 配置值或占位符表达式
     */
    String value();
}
