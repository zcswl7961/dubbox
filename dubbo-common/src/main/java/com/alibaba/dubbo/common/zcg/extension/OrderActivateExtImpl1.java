package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.Activate;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 */
@Activate(order = 1, group = {"order"})
public class OrderActivateExtImpl1 implements ActivateExtension{
    @Override
    public String echo(String meg) {
        return meg;
    }
}
