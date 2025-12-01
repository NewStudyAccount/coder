package com.example.demo.simplespring;

import java.lang.annotation.*;

/**
 * 自定义@Component注解，标记为Bean
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";
}