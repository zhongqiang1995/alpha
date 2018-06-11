package com.azl.business.call;

import java.util.List;

/**
 * Created by zhong on 2017/2/17.
 */

public abstract class BusinessListCallBack<T>  extends BaseBusinessCallBack{
    private int mPageCount;
    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int mPageCount) {
        this.mPageCount = mPageCount;
    }

    public abstract void onError(int code);

    public  void onError(int code,String codeStr){};

    public abstract void onSuccess(List<T> data);

    public abstract void onNext(List<T> data);

    public abstract void onOther(Object[] args, boolean isCache);

    public abstract void onComplete();

    public abstract void onCache(List<T> data);
}
