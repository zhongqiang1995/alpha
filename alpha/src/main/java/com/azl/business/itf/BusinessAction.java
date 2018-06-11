package com.azl.business.itf;


import com.azl.business.call.BusinessListCallBack;

import java.util.List;

/**
 * Created by zhong on 2017/2/17.
 */

public abstract class BusinessAction<T> extends BaseBusiness {

    protected static final int DEFAULT_PAGE = 1;
    protected static final int DEFAULT_PAGE_COUNT = 10;
    protected int mPage = DEFAULT_PAGE;
    protected BusinessListCallBack<T> mCallBack;

    public BusinessAction(BusinessListCallBack callBack) {
        setCallBack(callBack);
    }

    protected abstract void loadData(int page, int pageCount);

    public int getCurrentPage() {
        return mPage;
    }

    public void setCurrentPage(int currentPage) {
        this.mPage = currentPage;
    }

    public void refresh() {
        mPage = getDefaultPage();
        execute();
    }

    public void next() {
        mPage++;
        execute();
    }

    public int getDefaultPage() {
        return DEFAULT_PAGE;
    }

    public int getDefaultPageCount() {
        return DEFAULT_PAGE_COUNT;
    }

    private void execute() {
        loadData(getCurrentPage(), getDefaultPageCount());
    }

    ;

    public void setCallBack(BusinessListCallBack<T> call) {
        this.mCallBack = call;
    }

    public BusinessListCallBack<T> getCallBack() {
        return mCallBack;
    }

    public void executeOnError(int code) {
        if(getCurrentPage()>getDefaultPage()){
            setCurrentPage(--mPage);
        }
        if (getCallBack() != null) {
            getCallBack().onError(code);
        }
    }

    public void executeOnError(int code, String codeStr) {
        if (getCallBack() != null) {
            getCallBack().onError(code, codeStr);
        }
    }

    public void executeOnComplete() {
        if (getCallBack() != null) {
            getCallBack().onComplete();
        }
    }

    public void executeOnSuccess(List<T> list) {
        if (getCallBack() != null) {
            getCallBack().onSuccess(list);
        }
    }

    public void executeOnNext(List<T> list) {
        if (getCallBack() != null) {
            getCallBack().onNext(list);
        }
    }

    public void executeOnOther(boolean isCache, Object... args) {
        if (getCallBack() != null) {
            getCallBack().onOther(args, isCache);
        }
    }

    public void executeOnCache(List<T> args) {
        if (getCallBack() != null) {
            getCallBack().onCache(args);
        }
    }


}
