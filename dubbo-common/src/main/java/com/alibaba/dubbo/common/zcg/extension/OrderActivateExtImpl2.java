package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.Activate;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 */
@Activate(order = 2, group = {"order"})
public class OrderActivateExtImpl2 implements ActivateExtension{
    @Override
    public String echo(String meg) {
        return meg;
    }
}
