package com.example.demo.simplespring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 销毁前执行注解
 * 标记在 Bean 销毁前要执行的清理方法
 * 类似于 JSR-250 的 @PreDestroy 注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
}
