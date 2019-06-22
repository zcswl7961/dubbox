package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: zhoucg
 * @date: 2019-06-19
 */
public class VersionTest {


    @Test
    public void test() throws IOException {

        Version.checkDuplicate(DubboNamespaceHandler.class);
    }
}
