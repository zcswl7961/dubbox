package com.alibaba.dubbo.common.bytecode;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Wrapper result
 * @author: zhoucg
 * @date: 2019-06-10
 */
public class WrapperTestResult {

    @Test
    public void testWrapperMain() {
        Wrapper wrapper = Wrapper.getWrapper(WrapperInterfaceB.class);
        String[] ns = wrapper.getDeclaredMethodNames();
        assertEquals(ns.length, 5);
        ns = wrapper.getMethodNames();
        assertEquals(ns.length, 6);
    }

    @Test
    public void testPulicMethod() {
        Method[] methods = WrapperInterfaceB.class.getMethods();
        assertEquals(6,methods.length);
    }


}
