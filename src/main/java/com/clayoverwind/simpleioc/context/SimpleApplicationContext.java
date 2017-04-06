package com.clayoverwind.simpleioc.context;

import com.clayoverwind.simpleioc.context.annotation.SimpleAutowired;
import com.clayoverwind.simpleioc.context.annotation.SimpleBean;
import com.clayoverwind.simpleioc.context.annotation.SimpleComponent;
import com.clayoverwind.simpleioc.context.factory.Bean;
import com.clayoverwind.simpleioc.util.ClassUtil;
import com.clayoverwind.simpleioc.util.ConcurrentHashSet;
import com.clayoverwind.simpleioc.util.LogUtil;
import com.clayoverwind.simpleioc.util.StringUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

public class SimpleApplicationContext implements ApplicationContext {

    private long startupDate;

    private Set<String> scannedPackages = new ConcurrentHashSet<>();

    private Map<String, Bean> registeredBeans = new ConcurrentHashMap<>();

    private Map<String, Bean> earlyBeans = new ConcurrentHashMap<>();

    private final Logger LOGGER = LogUtil.getLogger(this.getClass());

    AtomicLong totalBeanCount = new AtomicLong(0L);

    AtomicLong nameConflictCount = new AtomicLong(0L);

    @Override
    public Object getBean(String name) {
        return registeredBeans.get(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> type) {
        Bean bean = (Bean)getBean(name);
        return bean == null ? null : (type.isAssignableFrom(bean.getClazz()) ? type.cast(bean.getObject()) : null);
    }

    @Override
    public <T> T getBean(Class<T> type) {
        Map<String, T> map = getBeansOfType(type);
        return map.isEmpty() ? null : type.cast(map.values().toArray()[0]);
    }

    @Override
    public boolean containsBean(String name) {
        return getBean(name) != null;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> res = new HashMap<>();
        registeredBeans.entrySet().stream().filter(entry -> type.isAssignableFrom(entry.getValue().getClazz())).forEach(entry -> res.put(entry.getKey(), type.cast(entry.getValue().getObject())));
        return res;
    }

    @Override
    public void setStartupDate(long startupDate) {
        this.startupDate = startupDate;
    }

    @Override
    public long getStartupDate() {
        return startupDate;
    }

    /**
     * try to autowire those beans in earlyBeans
     * if succeed, remove it from earlyBeans and put it into registeredBeans
     * otherwise ,throw a RuntimeException(in autowireFields)
     */
    private synchronized void processEarlyBeans() {
        for (Map.Entry<String, Bean> entry : earlyBeans.entrySet()) {
            Bean myBean = entry.getValue();
            try {
                if (autowireFields(myBean.getObject(), myBean.getClazz(), true)) {
                    registeredBeans.put(entry.getKey(), myBean);
                    earlyBeans.remove(entry.getKey());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * scan base packages and create beans
     * @param basePackages
     * @param recursively
     * @throws ClassNotFoundException
     */
    public void scan(Set<String> basePackages, boolean recursively) throws ClassNotFoundException, IOException {
        LOGGER.info("start scanning......");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // get all classes who haven't been registered
        Set<Class<?>> classes = new LinkedHashSet<>();
        for (String packageName : basePackages) {
            if (scannedPackages.add(packageName)) {
                classes.addAll(ClassUtil.getClassesByPackageName(classLoader, packageName, recursively));
            }
        }

        // autowire or create bean for each class
        classes.forEach(this::processSingleClass);

        processEarlyBeans();

        LOGGER.info("scan over!");
    }

    /**
     * try to create a bean for certain class, put it into registeredBeans if success, otherwise put it into earlyBeans
     * @param clazz
     */
    private void processSingleClass(Class<?> clazz) {
        LOGGER.info(String.format("processSingleClass [%s] ...", clazz.getName()));

        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof SimpleComponent) {
                Object instance;
                try {
                    instance = clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                long beanId = totalBeanCount.getAndIncrement();
                SimpleComponent component = (SimpleComponent) annotation;
                String beanName = component.value();
                if (beanName.isEmpty()) {
                    beanName = getUniqueBeanNameByClassAndBeanId(clazz, beanId);
                }

                try {
                    if (autowireFields(instance, clazz, false)) {
                        registeredBeans.put(beanName, new Bean(instance, clazz));
                    } else {
                        earlyBeans.put(beanName, new Bean(instance, clazz));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                try {
                    createBeansByMethodsOfClass(instance, clazz);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void createBeansByMethodsOfClass(Object instance, Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getMethodsWithAnnotation(clazz, SimpleBean.class);
        for (Method method : methods) {
            method.setAccessible(true);
            Object methodBean = method.invoke(instance);
            long beanId = totalBeanCount.getAndIncrement();
            Class<?> methodBeanClass = methodBean.getClass();

            //bean name
            SimpleBean simpleBean = method.getAnnotation(SimpleBean.class);
            String beanName = simpleBean.value();
            if (beanName.isEmpty()) {
                beanName = getUniqueBeanNameByClassAndBeanId(clazz, beanId);
            }

            // register bean
            registeredBeans.put(beanName, new Bean(methodBean, methodBeanClass));
        }
    }

    private List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<?> annotationClass) {
        List<Method> res = new LinkedList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == annotationClass) {
                    res.add(method);
                    break;
                }
            }
        }
        return res;
    }


    /**
     * try autowire all fields of a certain instance
     * @param instance
     * @param clazz
     * @param lastChance
     * @return true if success, otherwise return false or throw a exception if this is the lastChance
     * @throws IllegalAccessException
     */
    private boolean autowireFields(Object instance, Class<?> clazz, boolean lastChance) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof SimpleAutowired) {
                    SimpleAutowired autowired = (SimpleAutowired) annotation;
                    String beanName = autowired.value();
                    Bean bean = getSimpleBeanByNameOrType(beanName, field.getType(), true);
                    if (bean == null) {
                        if (lastChance) {
                            if (!autowired.required()) {
                                break;
                            }
                            throw new RuntimeException(String.format("Failed in autowireFields : [%s].[%s]", clazz.getName(), field.getName()));
                        } else {
                            return false;
                        }
                    }
                    field.setAccessible(true);
                    field.set(instance, bean.getObject());
                }
            }
        }
        return true;
    }

    /**
     * only used in autowireFields
     * @param beanName
     * @param type
     * @param allowEarlyBean
     * @return
     */
    private Bean getSimpleBeanByNameOrType(String beanName, Class<?> type, boolean allowEarlyBean) {
        // 1. by name
        Bean res = registeredBeans.get(beanName);
        if (res == null && allowEarlyBean) {
            res = earlyBeans.get(beanName);
        }

        // 2. by type
        if (type != null) {
            if (res == null) {
                res = getSimpleBeanByType(type, registeredBeans);
            }
            if (res == null && allowEarlyBean) {
                res = getSimpleBeanByType(type, earlyBeans);
            }
        }

        return res;
    }

    /**
     * search bean by type in certain beans map
     * @param type
     * @param beansMap
     * @return
     */
    private Bean getSimpleBeanByType(Class<?> type, Map<String, Bean> beansMap) {
        List<Bean> beans = new LinkedList<>();
        beansMap.entrySet().stream().filter(entry -> type.isAssignableFrom(entry.getValue().getClazz())).forEach(entry -> beans.add(entry.getValue()));
        if (beans.size() > 1) {
            throw new RuntimeException(String.format("Autowire by type, but more than one instance of type [%s] is founded!", beans.get(0).getClazz().getName()));
        }
        return beans.isEmpty() ? null : beans.get(0);
    }

    private String getUniqueBeanNameByClassAndBeanId(Class<?> clazz, long beanId) {
        String beanName = clazz.getName() + "_" + beanId;
        while (registeredBeans.containsKey(beanName) || earlyBeans.containsKey(beanName)) {
            beanName = clazz.getName() + "_" + beanId + "_" + nameConflictCount.getAndIncrement();
        }
        return beanName;
    }
}