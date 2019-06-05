package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.Activate;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 */
@Activate(value = {"value1"}, group = {"value"})
public class ValueActivateExtImpl implements ActivateExtension {
    @Override
    public String echo(String meg) {
        return meg;
    }
}
