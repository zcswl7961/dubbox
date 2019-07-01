package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.zcg.extension.ActivateExtension;
import com.alibaba.dubbo.container.Container;
import com.alibaba.dubbo.container.LocalContainer;
import com.alibaba.dubbo.demo.bid.BidService;
import com.alibaba.dubbo.demo.bid.BidServiceImpl;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @author: zhoucg
 * @date: 2019-06-04
 */
public class ExtensionLoaderTestLocal {


    /**
     * ExtensionLoader.getAdaptiveExtension()
     */
    @Test
    public void testProtocol() {
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
//        Protocol protocol = loader.getAdaptiveExtension();
        Protocol dubboProtocol = loader.getExtension("dubbo");
        System.out.println("dubbo:"+dubboProtocol);
//        System.out.println(protocol);
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

    /**
     * dubbo类JDK SPI机制
     * https://blog.csdn.net/qiangcai/article/details/77750541
     */
    @Test
    public void testSpringContainer() {

        ExtensionLoader<Container> loader = ExtensionLoader.getExtensionLoader(Container.class);
        Container container = loader.getExtension("spring");
        System.out.println(container);


    }

    /**
     *
     */
    @Test
    public void testLocalContainer() {
        ExtensionLoader<LocalContainer> loader = ExtensionLoader.getExtensionLoader(LocalContainer.class);
        LocalContainer adaptiveExtension = loader.getAdaptiveExtension();
        LocalContainer localContainer = loader.getExtension("localContainer");
        System.out.println(localContainer);
        System.out.println(adaptiveExtension);
    }

    /**
     * 动态类的本质是可以做到一个SPI中的不同的Adaptive方法可以去调不同的SPI实现类去处理。
     * 使得程序的灵活性大大提高。这才是整套SPI设计的一个精华之所在
     */
    @Test
    public void testAdatpiveExtensionFactory() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        ExtensionFactory extensionFactory = loader.getAdaptiveExtension();

    }

    @Test
    public void testProxyFactory() throws MalformedURLException {
        ExtensionLoader<ProxyFactory> loader = ExtensionLoader.getExtensionLoader(ProxyFactory.class);
        System.out.println(loader);
        ProxyFactory proxyFactory = loader.getAdaptiveExtension();
        BidServiceImpl bidService = new BidServiceImpl();
        URL uri = URL.valueOf("injvm://127.0.0.1/com.alibaba.dubbo.demo.bid.BidService?anyhost=true&application=demo-provider&dubbo=2.0.0&generic=false&interface=com.alibaba.dubbo.demo.bid.BidService&methods=throwNPE,bid&organization=dubbox&owner=programmer&pid=20752&serialization=kryo&side=provider&timestamp=1559731773054");
        if(proxyFactory.getClass() == JavassistProxyFactory.class) {
            System.out.println(true);
        }
        proxyFactory.getInvoker(bidService, BidService.class,uri);
        System.out.println(proxyFactory);
    }

    /**
     * 关于@Activate测试
     * 从上面的几个测试用例，可以得到下面的结论：
     *  1. 根据loader.getActivateExtension中的group和搜索到此类型的实例进行比较，如果group能匹配到，就是我们选择的，也就是在此条件下需要激活的。
     *  2. @Activate中的value是参数是第二层过滤参数（第一层是通过group），在group校验通过的前提下，如果URL中的参数（k）与值（v）中的参数名同@Activate中的value值一致或者包含，那么才会被选中。相当于加入了value后，条件更为苛刻点，需要URL中有此参数并且，参数必须有值。
     *  3. @Activate的order参数对于同一个类型的多个扩展来说，order值越小，优先级越高。
     */
    @Test
    public void testDefault() {

        //@Activate注解中声明group
        ExtensionLoader<ActivateExtension> loader = ExtensionLoader.getExtensionLoader(ActivateExtension.class);
        URL url = URL.valueOf("test://localhost/test");
        //查询组为defaul_group的ActivateExtension实现
        List<ActivateExtension> list = loader.getActivateExtension(url,new String[]{},"default_group");
        System.out.println(list.size());
        System.out.println(list.get(0).getClass());

        //@Activate注解中声明多个group
        List<ActivateExtension> list1 = loader.getActivateExtension(url, new String[]{}, "group2");
        Assert.assertEquals(1,list1.size());
        Assert.assertEquals("com.alibaba.dubbo.common.zcg.extension.GroupActivateExtImpl",list1.get(0).getClass().getName());

        //@Activate注解中声明了group与value
        url.addParameter("value1","value");
        List<ActivateExtension> list2 = loader.getActivateExtension(url, new String[]{}, "value");
        Assert.assertEquals(1,list2.size());
        Assert.assertEquals("com.alibaba.dubbo.common.zcg.extension.ValueActivateExtImpl",list2.get(0).getClass().getName());

        //@Activate注解中声明了order,低的排序优先级搞
        List<ActivateExtension> list3 = loader.getActivateExtension(url, new String[]{}, "order");
        Assert.assertEquals(2,list3.size());
        Assert.assertEquals("com.alibaba.dubbo.common.zcg.extension.OrderActivateExtImpl1",list3.get(0).getClass().getName());
        Assert.assertEquals("com.alibaba.dubbo.common.zcg.extension.OrderActivateExtImpl2",list3.get(1).getClass().getName());

    }

    @Test
    public void testProtocolAdaptive() {

        ExtensionLoader<Protocol> extensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Protocol protocol = extensionLoader.getExtension("dubbo");
        System.out.println(protocol);
//        Protocol protocol = extensionLoader.getAdaptiveExtension();

    }

}
