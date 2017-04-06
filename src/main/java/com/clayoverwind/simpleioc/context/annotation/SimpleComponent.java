package com.clayoverwind.simpleioc.context.annotation;

import java.lang.annotation.*;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleComponent {
    String value() default "";
}