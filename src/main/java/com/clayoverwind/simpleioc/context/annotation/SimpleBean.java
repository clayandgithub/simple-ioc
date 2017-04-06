package com.clayoverwind.simpleioc.context.annotation;

import java.lang.annotation.*;

/**
 * @author clayoverwind
 * @version 2017/4/6
 * @E-mail clayanddev@163.com
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleBean {
    String value() default "";
}
