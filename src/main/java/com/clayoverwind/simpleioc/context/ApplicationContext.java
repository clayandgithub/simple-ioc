package com.clayoverwind.simpleioc.context;

import com.clayoverwind.simpleioc.context.factory.BeanFactory;
import com.clayoverwind.simpleioc.context.factory.ListableBeanFactory;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */
public interface ApplicationContext extends ListableBeanFactory {

    void setStartupDate(long startupDate);

    long getStartupDate();
}
