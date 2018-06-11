package com.azl.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhong on 2017/5/23.
 */

public class CacheThreadPoll {
    private static CacheThreadPoll mInstance;
    private static ExecutorService mService;

    private CacheThreadPoll() {
    }

    public static CacheThreadPoll getInstance() {
        if (mInstance == null) {
            synchronized (CacheThreadPoll.class) {
                if (mInstance == null) {
                    mInstance = new CacheThreadPoll();
                    mService = Executors.newCachedThreadPool();
                }
            }
        }
        return mInstance;
    }

    public void submit(Runnable runnable) {
        mService.submit(runnable);
    }


}
