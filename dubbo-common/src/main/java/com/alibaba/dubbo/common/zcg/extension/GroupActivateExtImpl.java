package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.Activate;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 */
@Activate(group = {"group1", "group2"})
public class GroupActivateExtImpl implements ActivateExtension{
    @Override
    public String echo(String meg) {
        return meg;
    }
}
