package com.azl.obs.retrofit.helper;

import com.azl.obs.retrofit.handle.ObsHandle;

import java.lang.reflect.Proxy;

/**
 * Created by zhong on 2017/5/16.
 */

public class ProxyHelper {

    protected static <T> T getProxy(Class<T> c, ConstructionImpl.Build build) {
        return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, new ObsHandle(build));
    }
}
