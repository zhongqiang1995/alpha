package com.azl.business.call;

/**
 * Created by zhong on 2017/2/22.
 */

public abstract class BusinessPagerCallBack<T> extends BaseBusinessCallBack {
    public abstract void onError(int code);

    public abstract void onSuccess(T data);

    public abstract void onComplete();

    public abstract void onCache(T data);
}
