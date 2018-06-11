package com.azl.obs.retrofit.itf;

import com.azl.file.bean.Info;

/**
 * Created by zhong on 2017/6/15.
 */

public abstract class FileObserver implements BaseObserver<Info> {

    @Override
    public abstract void onNext(Info object);

    public abstract void onError(int code, Info info);

    public void progress() {
    }

    @Override
    public void onBegin() {

    }


    @Override
    public void onComplete() {

    }
}
