package com.example.demo.simplespring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动装配注解 - 标记需要自动注入的字段
 * 类似于 Spring 的 @Autowired 注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    
    /**
     * 是否必须
     * 如果为 true，找不到依赖时抛出异常
     * 如果为 false，找不到依赖时忽略
     */
    boolean required() default true;
}
