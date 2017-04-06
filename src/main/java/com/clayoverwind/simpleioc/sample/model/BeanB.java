package com.clayoverwind.simpleioc.sample.model;

import com.clayoverwind.simpleioc.context.annotation.SimpleAutowired;
import com.clayoverwind.simpleioc.context.annotation.SimpleComponent;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

@SimpleComponent
public class BeanB {
    private String content;

    @SimpleAutowired
    private BeanA beanA;

    public BeanB(){}

    public BeanB(String content) {
        this.content = content;
    }

    public void print() {
        System.out.printf("[BeanB].beanA = %s\n", beanA.toString());
    }
}
