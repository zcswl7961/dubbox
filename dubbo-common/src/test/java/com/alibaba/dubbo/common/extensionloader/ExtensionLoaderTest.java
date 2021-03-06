/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.extensionloader;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extensionloader.activate.ActivateExt1;
import com.alibaba.dubbo.common.extensionloader.activate.impl.ActivateExt1Impl1;
import com.alibaba.dubbo.common.extensionloader.activate.impl.GroupActivateExtImpl;
import com.alibaba.dubbo.common.extensionloader.activate.impl.OrderActivateExtImpl1;
import com.alibaba.dubbo.common.extensionloader.activate.impl.OrderActivateExtImpl2;
import com.alibaba.dubbo.common.extensionloader.activate.impl.ValueActivateExtImpl;
import com.alibaba.dubbo.common.extensionloader.ext1.impl.SimpleExtImpl1;
import com.alibaba.dubbo.common.extensionloader.ext6_wrap.WrappedExt;
import com.alibaba.dubbo.common.extensionloader.ext7.InitErrorExt;
import com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt1;
import com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt2;
import com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt3;
import com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt4;
import com.alibaba.dubbo.common.extensionloader.ext8_add.impl.*;
import junit.framework.Assert;

import org.junit.Test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt;
import com.alibaba.dubbo.common.extensionloader.ext1.impl.SimpleExtImpl2;
import com.alibaba.dubbo.common.extensionloader.ext2.Ext2;
import com.alibaba.dubbo.common.extensionloader.ext6_wrap.impl.Ext5Wrapper1;
import com.alibaba.dubbo.common.extensionloader.ext6_wrap.impl.Ext5Wrapper2;

/**
 * ExtensionLoader.getExtensionLoader(Class class);
 * 1,class是接口类型
 * 2,class是含有SPI注解的
 * 3,class不能为null
 * @author ding.lid
 */
public class ExtensionLoaderTest {
    @Test
    public void test_getExtensionLoader_Null() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    containsString("Extension type == null"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotInterface() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(ExtensionLoaderTest.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    containsString("Extension type(class com.alibaba.dubbo.common.extensionloader.ExtensionLoaderTest) is not interface"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotSpiAnnotation() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(NoSpiExt.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    allOf(containsString("com.alibaba.dubbo.common.extensionloader.NoSpiExt"),
                            containsString("is not extension"),
                            containsString("WITHOUT @SPI Annotation")));
        }
    }

    /**
     * 获取默认的Extension
     * @throws Exception
     */
    @Test
    public void test_getDefaultExtension() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtension();
        assertThat(ext, instanceOf(SimpleExtImpl1.class));
        
        String name = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtensionName();
        assertEquals("impl1", name);
    }

    /**
     * 没有对应的默认实现的情况下
     * @throws Exception
     */
    @Test
    public void test_getDefaultExtension_NULL() throws Exception {
        Ext2 ext = ExtensionLoader.getExtensionLoader(Ext2.class).getDefaultExtension();
        assertNull(ext);
        
        String name = ExtensionLoader.getExtensionLoader(Ext2.class).getDefaultExtensionName();
        assertNull(name);
    }
    
    @Test
    public void test_getExtension() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl1") instanceof SimpleExtImpl1);
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl2") instanceof SimpleExtImpl2);
    }
    
    @Test
    public void test_getExtension_WithWrapper() throws Exception {
        WrappedExt impl1 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl1");
        assertThat(impl1, anyOf(instanceOf(Ext5Wrapper1.class), instanceOf(Ext5Wrapper2.class)));
        
        WrappedExt impl2 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl2") ;
        assertThat(impl2, anyOf(instanceOf(Ext5Wrapper1.class), instanceOf(Ext5Wrapper2.class)));
        
        
        URL url = new URL("p1", "1.2.3.4", 1010, "path1");
        int echoCount1 = Ext5Wrapper1.echoCount.get();
        int echoCount2 = Ext5Wrapper2.echoCount.get();

        assertEquals("Ext5Impl1-echo", impl1.echo(url, "ha"));
        assertEquals(echoCount1 + 1, Ext5Wrapper1.echoCount.get());
        assertEquals(echoCount2 + 1, Ext5Wrapper2.echoCount.get());
    }
    
    @Test
    public void test_getExtension_ExceptionNoExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("XXX");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt by name XXX"));
        }
    }
    
    @Test
    public void test_getExtension_ExceptionNoExtension_WrapperNotAffactName() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("XXX");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.alibaba.dubbo.common.extensionloader.ext6_wrap.WrappedExt by name XXX"));
        }
    }
    
    @Test
    public void test_getExtension_ExceptionNullArg() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }
    
    @Test
    public void test_hasExtension() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("impl1"));
        assertFalse(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("impl1,impl2"));
        assertFalse(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("xxx"));
        
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    public void test_hasExtension_wrapperIsNotExt() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1"));
        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1,impl2"));
        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("xxx"));

        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("wrapper1"));

        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }
    
    @Test
    public void test_getSupportedExtensions() throws Exception {
        Set<String> exts = ExtensionLoader.getExtensionLoader(SimpleExt.class).getSupportedExtensions();
        
        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");
        expected.add("impl3");
        
        assertEquals(expected, exts);
    }

    @Test
    public void test_getSupportedExtensions_wrapperIsNotExt() throws Exception {
        Set<String> exts = ExtensionLoader.getExtensionLoader(WrappedExt.class).getSupportedExtensions();

        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");

        assertEquals(expected, exts);
    }

    @Test
    public void test_AddExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual1");
            fail();
        }
        catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt1 by name Manual"));
        }

        ExtensionLoader.getExtensionLoader(AddExt1.class).addExtension("Manual1", AddExt1_ManualAdd1.class);
        AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual1");

        assertThat(ext, instanceOf(AddExt1_ManualAdd1.class));
        assertEquals("Manual1", ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd1.class));
    }

    @Test
    public void test_AddExtension_ExceptionWhenExistedExtension() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl1");

        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).addExtension("impl1", AddExt1_ManualAdd1.class);
            fail();
        }
        catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Extension name impl1 already existed(Extension interface com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt1)!"));
        }
    }

    @Test
    public void test_AddExtension_Adaptive() throws Exception {
        ExtensionLoader<AddExt2> loader = ExtensionLoader.getExtensionLoader(AddExt2.class);
        loader.addExtension(null, AddExt2_ManualAdaptive.class);

        AddExt2 adaptive = loader.getAdaptiveExtension();
        assertTrue(adaptive instanceof AddExt2_ManualAdaptive);
    }

    @Test
    public void test_AddExtension_Adaptive_ExceptionWhenExistedAdaptive() throws Exception {
        ExtensionLoader<AddExt1> loader = ExtensionLoader.getExtensionLoader(AddExt1.class);

        loader.getAdaptiveExtension();

        try {
            loader.addExtension(null, AddExt1_ManualAdaptive.class);
            fail();
        }
        catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Adaptive Extension already existed(Extension interface com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt1)!"));
        }
    }

    @Test
    public void test_replaceExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual2");
            fail();
        }
        catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt1 by name Manual"));
        }

        {
            AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");

            assertThat(ext, instanceOf(AddExt1Impl1.class));
            assertEquals("impl1", ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1Impl1.class));
        }
        {
            ExtensionLoader.getExtensionLoader(AddExt1.class).replaceExtension("impl1", AddExt1_ManualAdd2.class);
            AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");

            assertThat(ext, instanceOf(AddExt1_ManualAdd2.class));
            assertEquals("impl1", ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd2.class));
        }
    }

    @Test
    public void test_replaceExtension_Adaptive() throws Exception {
        ExtensionLoader<AddExt3> loader = ExtensionLoader.getExtensionLoader(AddExt3.class);

        AddExt3 adaptive = loader.getAdaptiveExtension();
        assertFalse(adaptive instanceof AddExt3_ManualAdaptive);

        loader.replaceExtension(null, AddExt3_ManualAdaptive.class);

        adaptive = loader.getAdaptiveExtension();
        assertTrue(adaptive instanceof AddExt3_ManualAdaptive);
    }

    @Test
    public void test_replaceExtension_ExceptionWhenNotExistedExtension() throws Exception {
        AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");

        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).replaceExtension("NotExistedExtension", AddExt1_ManualAdd1.class);
            fail();
        }
        catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Extension name NotExistedExtension not existed(Extension interface com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt1)"));
        }
    }

    @Test
    public void test_replaceExtension_Adaptive_ExceptionWhenNotExistedExtension() throws Exception {
        ExtensionLoader<AddExt4> loader = ExtensionLoader.getExtensionLoader(AddExt4.class);

        try {
            loader.replaceExtension(null, AddExt4_ManualAdaptive.class);
            fail();
        }
        catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Adaptive Extension not existed(Extension interface com.alibaba.dubbo.common.extensionloader.ext8_add.AddExt4)"));
        }
    }

    @Test
    public void test_InitError() throws Exception {
        ExtensionLoader<InitErrorExt> loader = ExtensionLoader.getExtensionLoader(InitErrorExt.class);
        
        loader.getExtension("ok");
        
        try {
            loader.getExtension("error");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Failed to load extension class(interface: interface com.alibaba.dubbo.common.extensionloader.ext7.InitErrorExt"));
            assertThat(expected.getCause(), instanceOf(ExceptionInInitializerError.class));
        }
    }

    /**
     * 基础测试模块
     * Dubbo SPI之Activate
     * 1. 根据loader.getActivateExtension中的group和搜索到此类型的实例进行比较，如果group能匹配到，就是我们选择的，也就是在此条件下需要激活的
     * 2. @Activate中的value是参数是第二层过滤参数（第一层是通过group）
     * 3. 在group校验通过的前提下，如果URL中的参数（k）与值（v）中的参数名同@Activate中的value值一致或者包含，那么才会被选中 ?
     * 4. @Activate的order参数对于同一个类型的多个扩展来说，order值越小，优先级越高。
     * @throws Exception
     */
    @Test
    public void testLoadActivateExtension() throws Exception {
        // test default
        URL url = URL.valueOf("test://localhost/test");
        List<ActivateExt1> list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, new String[]{}, "default_group");
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.get(0).getClass() == ActivateExt1Impl1.class);

        // test group
        url = url.addParameter("zcg","value的数据");
        url = url.addParameter("wl","wl的数据");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, new String[]{}, "group1");
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.get(0).getClass() == GroupActivateExtImpl.class);

        // test value
        url = url.removeParameter(Constants.GROUP_KEY);
        url = url.addParameter(Constants.GROUP_KEY, "value2");
        url = url.addParameter("value", "测试");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, new String[]{"wl","zcg"}, "group");
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.get(0).getClass() == ValueActivateExtImpl.class);



        // test order
        url = URL.valueOf("test://localhost/test");
        url = url.addParameter(Constants.GROUP_KEY, "order");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, new String[]{}, "order");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).getClass() == OrderActivateExtImpl1.class);
        Assert.assertTrue(list.get(1).getClass() == OrderActivateExtImpl2.class);
    }

    /**
     * ExtensionLoader.getActivateExtension(url,String key,String group)
     * 1,首先是匹配出SPI扩展类中所有符合group的数据，加入到list中 --> 就是SPI扩展类的实现类中，定义了@Activate(group="group"),group属性相等
     * 2,然后是如果传入的第二个参数是key，首先是从url中getParameter(key)，获取对应的values，封装成values，
     * 3,接着是找到SPI扩展文件中，对应的value的扩展实现类
     * @throws Exception
     */
    @Test
    public void testLoadDefaultActivateExtension() throws Exception {
        // test default
        URL url = URL.valueOf("test://localhost/test?ext=order1,order2");
        List<ActivateExt1> list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, "ext", "leronjames");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).getClass() == OrderActivateExtImpl1.class);
        Assert.assertTrue(list.get(1).getClass() == ActivateExt1Impl1.class);
        
        url = URL.valueOf("test://localhost/test?ext=default,order1");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, "ext", "default_group");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).getClass() == ActivateExt1Impl1.class);
        Assert.assertTrue(list.get(1).getClass() == OrderActivateExtImpl1.class);
    }

    @Test
    public void getAdaptive() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        ExtensionFactory extensionFactory = loader.getExtension("dubbo");
        System.out.println(extensionFactory);

    }

}