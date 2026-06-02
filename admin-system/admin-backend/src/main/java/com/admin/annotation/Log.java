package com.admin.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    String title() default "";

    int businessType() default 0;

    boolean isSaveRequestData() default true;

    boolean isSaveResponseData() default true;
}
