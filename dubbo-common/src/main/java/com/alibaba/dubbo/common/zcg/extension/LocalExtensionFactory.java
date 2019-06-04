package com.alibaba.dubbo.common.zcg.extension;

import com.alibaba.dubbo.common.extension.ExtensionFactory;

/**
 * 自定义的LocalExtensionFactory
 * @author: zhoucg
 * @date: 2019-06-04
 */
public class LocalExtensionFactory implements ExtensionFactory {
    @Override
    public <T> T getExtension(Class<T> type, String name) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
