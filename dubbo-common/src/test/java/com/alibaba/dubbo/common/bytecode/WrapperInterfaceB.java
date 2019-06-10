package com.alibaba.dubbo.common.bytecode;

/**
 * @author: zhoucg
 * @date: 2019-06-10
 */
public interface WrapperInterfaceB extends WrapperInterfaceA{

    void setName(String name);

    void hello(String name);

    int showInt(int v);

    void setFloat(float f);

    float getFloat();
}
