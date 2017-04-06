package com.clayoverwind.simpleioc.boot;

import java.lang.annotation.*;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleIocBootApplication {
    String[] basePackages() default {};
}
