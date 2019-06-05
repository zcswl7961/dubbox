package com.alibaba.dubbo.container;

import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 */
@SPI
public interface LocalContainer {

    @Adaptive
    String getAdatpive(String a);
}
