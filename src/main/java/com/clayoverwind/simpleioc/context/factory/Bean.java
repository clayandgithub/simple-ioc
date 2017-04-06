package com.clayoverwind.simpleioc.context.factory;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

public class Bean {
    private Object object;

    private Class<?> clazz;

    public Bean(Object object, Class<?> clazz) {
        this.object = object;
        this.clazz = clazz;
    }

    public Object getObject() {
        return object;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
