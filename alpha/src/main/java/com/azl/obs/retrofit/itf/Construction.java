package com.azl.obs.retrofit.itf;

/**
 * Created by zhong on 2017/5/16.
 */

public interface Construction {
    <T> T create(Class<T> t);
}
