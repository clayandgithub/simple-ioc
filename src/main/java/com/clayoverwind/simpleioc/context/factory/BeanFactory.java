package com.clayoverwind.simpleioc.context.factory;

/**
 * Created by wangweiwei on 2017/4/6.
 */
public interface BeanFactory {
    Object getBean(String var1);

    <T> T getBean(String name, Class<T> requiredType);

    <T> T getBean(Class<T> name);

    boolean containsBean(String var1);
}
