package com.alibaba.dubbo.common.bytecode;

/**
 * @author: zhoucg
 * @date: 2019-06-10
 */
public abstract class WrapperInterfaceImpl implements WrapperInterfaceB{

    private String name = "you name";

    private float fv = 0;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void hello(String name)
    {
        System.out.println("hello " + name);
    }

    public int showInt(int v)
    {
        return v;
    }

    public float getFloat()
    {
        return fv;
    }

    public void setFloat(float f)
    {
        fv = f;
    }
}
