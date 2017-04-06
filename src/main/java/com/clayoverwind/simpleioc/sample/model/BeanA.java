package com.clayoverwind.simpleioc.sample.model;

import com.clayoverwind.simpleioc.context.annotation.SimpleAutowired;
import com.clayoverwind.simpleioc.context.annotation.SimpleComponent;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

@SimpleComponent
public class BeanA {
    private String content;

    @SimpleAutowired
    private BeanB beanB;

    public BeanA(){}

    public BeanA(String content) {
        this.content = content;
    }

    public void print() {
        System.out.printf("[BeanA].beanB = %s\n", beanB.toString());
    }
}
