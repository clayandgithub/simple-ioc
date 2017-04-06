package com.clayoverwind.simpleioc.context.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author clayoverwind
 * @version 2017/4/6
 * @E-mail clayanddev@163.com
 */
public interface ListableBeanFactory extends BeanFactory {
    <T> Map<String, T> getBeansOfType(Class<T> type);
}
