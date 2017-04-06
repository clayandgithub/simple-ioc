package com.clayoverwind.simpleioc.context;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author clayoverwind
 * @version 2017/4/6
 * @E-mail clayanddev@163.com
 */
public class SimpleApplicationContextInitializer implements ApplicationContextInitializer<SimpleApplicationContext> {

    private Set<String> basePackages = new LinkedHashSet<>();

    public SimpleApplicationContextInitializer(List<String> basePackages) {
        this.basePackages.addAll(basePackages);
    }

    @Override
    public void initialize(SimpleApplicationContext applicationContext) {
        try {
            applicationContext.scan(basePackages, true);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        applicationContext.setStartupDate(System.currentTimeMillis());
    }
}
