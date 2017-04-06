package com.clayoverwind.simpleioc.boot;

import com.clayoverwind.simpleioc.context.*;
import com.clayoverwind.simpleioc.util.LogUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

public class SimpleIocApplication {
    private Class<?> applicationEntryClass;

    private ApplicationContext applicationContext;

    private final Logger LOGGER = LogUtil.getLogger(this.getClass());

    public SimpleIocApplication(Class<?> applicationEntryClass) {
        this.applicationEntryClass = applicationEntryClass;
    }

    public static void run(Class<?> applicationEntryClass, String[] args) {
        new SimpleIocApplication(applicationEntryClass).run(args);
    }

    public void run(String[] args) {
        LOGGER.info("start running......");

        // create application context and application initializer
        applicationContext = createSimpleApplicationContext();
        ApplicationContextInitializer initializer = createSimpleApplicationContextInitializer(applicationEntryClass);

        // initialize the application context (this is where we create beans)
        initializer.initialize(applicationContext); // here maybe exist a hidden cast

        // process those special beans
        processSpecialBeans(args);

        LOGGER.info("over!");
    }

    private SimpleApplicationContextInitializer createSimpleApplicationContextInitializer(Class<?> entryClass) {
        // get base packages
        SimpleIocBootApplication annotation = entryClass.getDeclaredAnnotation(SimpleIocBootApplication.class);
        String[] basePackages = annotation.basePackages();
        if (basePackages.length == 0) {
            basePackages = new String[]{entryClass.getPackage().getName()};
        }

        // create context initializer with base packages
        return new SimpleApplicationContextInitializer(Arrays.asList(basePackages));
    }

    private SimpleApplicationContext createSimpleApplicationContext() {
        return new SimpleApplicationContext();
    }

    private void processSpecialBeans(String[] args) {
        callRegisteredRunners(args);
    }

    private void callRegisteredRunners(String[] args) {
        Map<String, SimpleIocApplicationRunner> applicationRunners = applicationContext.getBeansOfType(SimpleIocApplicationRunner.class);
        try {
            for (SimpleIocApplicationRunner applicationRunner : applicationRunners.values()) {
                applicationRunner.run(args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}