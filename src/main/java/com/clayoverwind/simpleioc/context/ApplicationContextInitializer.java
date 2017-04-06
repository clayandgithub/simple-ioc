package com.clayoverwind.simpleioc.context;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */
public interface ApplicationContextInitializer<C extends ApplicationContext> {
    void initialize(C applicationContext);
}