package com.admin.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    String deptAlias() default "d";

    String userAlias() default "u";

    int dataScope() default 0;
}
