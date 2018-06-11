package com.azl.obs.retrofit.itf;

/**
 * Created by zhong on 2017/6/15.
 */

public interface BaseObserver<T> {
    void onComplete();
    void onBegin();
    void onNext(T t);
}
