package com.azl.obs.retrofit.itf;

/**
 * Created by zhong on 2017/5/15.
 */

public interface Observer<T> extends BaseObserver<T>{
    void onCache(T t);

    void onError(int code, String msg);


}
