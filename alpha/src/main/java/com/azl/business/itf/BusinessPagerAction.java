package com.azl.business.itf;


import com.azl.business.call.BusinessPagerCallBack;

/**
 * Created by zhong on 2017/2/22.
 */

public abstract class BusinessPagerAction<T> extends BaseBusiness {

    private BusinessPagerCallBack<T> mCallBack;

    public BusinessPagerAction(BusinessPagerCallBack callBack) {
        setCallBack(callBack);
    }

    public void setCallBack(BusinessPagerCallBack<T> call) {
        this.mCallBack = call;
    }

    public BusinessPagerCallBack<T> getCallBack() {
        return mCallBack;
    }

    public void execute() {
        loadData();
    }

    protected abstract void loadData();

    public void executeOnError(int code) {
        if (getCallBack() != null) {
            getCallBack().onError(code);
        }
    }

    public void executeOnComplete() {
        if (getCallBack() != null) {
            getCallBack().onComplete();
        }
    }

    public void executeOnSuccess(T list) {
        if (getCallBack() != null) {
            getCallBack().onSuccess(list);
        }
    }


    public void executeOnCache(T data) {
        if (getCallBack() != null) {
            getCallBack().onCache(data);
        }
    }
}
