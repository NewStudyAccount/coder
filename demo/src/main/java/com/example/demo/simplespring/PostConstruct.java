package com.example.demo.simplespring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 初始化后执行注解
 * 标记在 Bean 实例化和依赖注入完成后要执行的方法
 * 类似于 JSR-250 的 @PostConstruct 注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
