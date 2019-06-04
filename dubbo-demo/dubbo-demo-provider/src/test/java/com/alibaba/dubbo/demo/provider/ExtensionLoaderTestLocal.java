package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.container.Container;
import com.alibaba.dubbo.rpc.Protocol;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.MarshalException;

/**
 * @author: zhoucg
 * @date: 2019-06-04
 */
public class ExtensionLoaderTestLocal {


    /**
     * dubbo类JDK SPI机制
     * https://blog.csdn.net/qiangcai/article/details/77750541
     */
    @Test
    public void testget() {
        ExtensionLoader<Container> loader = ExtensionLoader.getExtensionLoader(Container.class);
        Container container = loader.getAdaptiveExtension();
        System.out.println(container);
    }

    @Test
    public void testProtocol() {
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Protocol protocol = loader.getExtension("dubbo");
    }

    @Test
    public void testMethod() {
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Protocol protocol = loader.getExtension("dubbo"); //DubboProtocol
        for(Method method : protocol.getClass().getMethods()) {
            if (method.getName().startsWith("set")
                    && method.getParameterTypes().length == 1
                    && Modifier.isPublic(method.getModifiers())) {
                System.out.println(method.getName());
                Class<?> pt = method.getParameterTypes()[0];//获取对应的参数的类型
                System.out.println("paramName"+pt.getName());
            }
        }
    }
}
