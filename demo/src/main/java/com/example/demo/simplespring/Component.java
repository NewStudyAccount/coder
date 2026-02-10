package com.example.demo.simplespring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件注解 - 标记一个类为 Spring Bean
 * 类似于 Spring 的 @Component 注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    
    /**
     * Bean 的名称
     * 如果不指定，默认使用类名首字母小写
     */
    String value() default "";
}
