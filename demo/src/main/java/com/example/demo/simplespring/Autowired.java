package com.example.demo.simplespring;

import java.lang.annotation.*;

/**
 * 自定义@Autowired注解，标记需要注入的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}