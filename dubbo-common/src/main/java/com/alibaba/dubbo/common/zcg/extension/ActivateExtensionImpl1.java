package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.Activate;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 */
@Activate(group = {"default_group"})
public class ActivateExtensionImpl1 implements ActivateExtension{
    @Override
    public String echo(String meg) {
        return meg;
    }
}
