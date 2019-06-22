package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author: zhoucg
 * @date: 2019-06-19
 */
public class ClassLoaderResourceTest {

    @Test
    public void test() throws IOException {

        //cls.getName().replace('.', '/') + ".class"
        String classNamePath = DubboNamespaceHandler.class.getName().replace(".","/") + ".class";
        Enumeration<URL> urls = VersionTest.class.getClassLoader().getResources(classNamePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            System.out.println(url.getPath());
        }
    }
}
