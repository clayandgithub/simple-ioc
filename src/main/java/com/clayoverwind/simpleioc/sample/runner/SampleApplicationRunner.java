package com.clayoverwind.simpleioc.sample.runner;

import com.clayoverwind.simpleioc.boot.SimpleIocApplicationRunner;
import com.clayoverwind.simpleioc.context.annotation.SimpleAutowired;
import com.clayoverwind.simpleioc.context.annotation.SimpleBean;
import com.clayoverwind.simpleioc.context.annotation.SimpleComponent;
import com.clayoverwind.simpleioc.sample.model.BeanA;
import com.clayoverwind.simpleioc.sample.model.BeanB;
import com.clayoverwind.simpleioc.sample.model.BeanC;

import java.beans.beancontext.BeanContext;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */
@SimpleComponent
public class SampleApplicationRunner  implements SimpleIocApplicationRunner {

    @SimpleAutowired
    private BeanA beanA;

    @SimpleAutowired
    private BeanB beanB;

    @SimpleAutowired("beanC1")
    private BeanC beanC1;

    @SimpleAutowired("beanC2")
    private BeanC beanC2;

    @Override
    public void run(String[] args) throws Exception {
        beanA.print();
        beanB.print();
        System.out.println(beanC1.toString());
        System.out.println(beanC2.toString());
    }

    @SimpleBean("beanC1")
    private BeanC createBeanC1() {
        return new BeanC("I am bean c1!");
    }

    @SimpleBean("beanC2")
    private BeanC createBeanC2() {
        return new BeanC("I am bean c2!");
    }
}
