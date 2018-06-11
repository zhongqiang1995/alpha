package com.azl.file.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhong on 2017/7/10.
 */

public class FileActionThreadPool {

    public synchronized static ExecutorService newThreadPool(int threadCount) {
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        return pool;
    }

}
