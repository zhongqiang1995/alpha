package com.azl.obs.data;

import com.azl.obs.retrofit.itf.BaseObserver;
import com.azl.obs.retrofit.itf.Observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhong on 2017/5/15.
 */

public abstract class DataGet {

    private List<BaseObserver> mObs = new ArrayList<>();


    public List<BaseObserver> getObs() {
        return mObs;
    }

    public abstract void execute(Map<String, String> parMap);

    public abstract void cancel();

    public abstract void execute();

    public void register(BaseObserver observer) {
        if (!mObs.contains(observer)) {
            mObs.add(observer);
        }
    }

    public boolean unRegister(BaseObserver observer) {
        if (mObs.contains(observer)) {
            return mObs.remove(observer);
        }
        return false;
    }

    protected void notifyNext(Object t) {
        for (BaseObserver obs : mObs) {
            obs.onNext(t);
        }
    }

    protected void notifyBegin() {
        for (BaseObserver obs : getObs()) {
            obs.onBegin();
        }
    }

    protected void notifyComplete() {
        for (BaseObserver obs : mObs) {
            obs.onComplete();
        }
    }


}
