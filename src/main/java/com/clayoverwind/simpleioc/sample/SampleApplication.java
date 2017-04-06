package com.clayoverwind.simpleioc.sample;

import com.clayoverwind.simpleioc.boot.SimpleIocApplication;
import com.clayoverwind.simpleioc.boot.SimpleIocApplicationRunner;
import com.clayoverwind.simpleioc.boot.SimpleIocBootApplication;
import com.clayoverwind.simpleioc.context.annotation.SimpleComponent;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */
@SimpleIocBootApplication
public class SampleApplication{

    public static void main(String[] args) {
        SimpleIocApplication.run(SampleApplication.class, args);
    }
}