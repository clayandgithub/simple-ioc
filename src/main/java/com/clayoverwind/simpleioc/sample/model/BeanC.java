package com.clayoverwind.simpleioc.sample.model;

/**
 * @author clayoverwind
 * @version 2017/4/6
 * @E-mail clayanddev@163.com
 */
public class BeanC {
    private String content;

    public BeanC(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BeanC.content = " + content;
    }

}
