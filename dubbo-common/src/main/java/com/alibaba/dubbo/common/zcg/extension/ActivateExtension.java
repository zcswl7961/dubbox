package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.SPI;

/**
 * @author: zhoucg
 * @date: 2019-06-05
 * ActiveExtension作为扩展的接口
 */
@SPI
public interface ActivateExtension {

    String echo(String meg);
}
