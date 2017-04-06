package com.clayoverwind.simpleioc.context.annotation;

import java.lang.annotation.*;

/**
 * @author clayoverwind
 * @version 2017/4/5
 * @E-mail clayanddev@163.com
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleAutowired {
    boolean required() default true;

    String value() default ""; // this field is moved from @Qualifier to here for simplicity
}