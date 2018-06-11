package com.azl.file.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.azl.api.AlphaApiService;
import com.azl.file.pool.FileActionThreadPool;
import com.azl.handle.action.HandleMsg;
import com.azl.handle.anno.Mark;
import com.azl.handle.anno.SelectThread;
import com.azl.handle.bean.ThreadMode;
import com.azl.obs.data.DataGet;
import com.azl.obs.data.HttpDataGet;
import com.azl.obs.data.OkHttpDownloadDataGet;
import com.azl.obs.data.OkHttpFileDataGet;
import com.azl.obs.data.OkHttpUploadDataGet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhong on 2017/7/10.
 */

public class FileActionService extends Service {
    private static final String TAG = "FileActionService";

    private static final String VALUE_ACTION = "action";
    private static final String VALUE_TYPE = "type";
    private static final String VALUE_LOCAL_PATH = "localPath";
    private static final String VALUE_PATH = "path";
    private static final String VALUE_MARK = "mark";
    private static final String VALUE_ID = "id";
    private static final String VALUE_TAG = "tag";


    private static final int SPEC_ADD = 1;//消息类型：添加任务
    private static final int SPEC_REDUCTION = 2;//消息类型：删除或者停止任务


    public static final int ACTION_REMOVE_ALL_ID = -2;//消息动作：删除所有任务


    private ExecutorService mDownloadPool;//下载线程池
    private ExecutorService mUploadPool;//上传线程池
    private List<OkHttpFileDataGet> mGetS;//保存下载中的任务
    private Map<OkHttpFileDataGet, Future> mCallS;

    /**
     * 添加一个上传或者下载任务
     *
     * @param context
     * @param path
     */
    public static void addTask(Context context, String localPath, String path, String mark, HttpDataGet.Type type, Serializable tab) {
        Intent intent = new Intent(context, FileActionService.class);
        intent.putExtra(VALUE_TYPE, type);
        intent.putExtra(VALUE_PATH, path);
        intent.putExtra(VALUE_LOCAL_PATH, localPath);
        intent.putExtra(VALUE_MARK, mark);
        intent.putExtra(VALUE_ACTION, SPEC_ADD);
        intent.putExtra(VALUE_TAG, tab);
        context.startService(intent);
    }

    /**
     * 删除正在运行或者已经添加的任务
     *
     * @param context
     * @param id
     * @param path
     */
    public static void removeTask(Context context, int id, String path, HttpDataGet.Type type) {
        Intent intent = new Intent(context, FileActionService.class);
        intent.putExtra(VALUE_ACTION, SPEC_REDUCTION);
        intent.putExtra(VALUE_ID, id);
        intent.putExtra(VALUE_PATH, path);
        intent.putExtra(VALUE_TYPE, type);
        context.startService(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent == null) return super.onStartCommand(intent, flags, startId);
            int action = intent.getIntExtra(VALUE_ACTION, -1);


            if (action == SPEC_ADD) {
                addTask(intent);
            } else if (action == SPEC_REDUCTION) {
                HttpDataGet.Type type = (HttpDataGet.Type) intent.getSerializableExtra(VALUE_TYPE);
                String path = "";
                path = intent.getStringExtra(VALUE_PATH);

                int id = intent.getIntExtra(VALUE_ID, -1);
                stopTask(id, path, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private synchronized void stopTask(int id, String path, HttpDataGet.Type type) {
        if (id == ACTION_REMOVE_ALL_ID) {
            stopAllTask(type);//停止所有下载
        } else if (id != -1) {
            stopIdTask(id);//停止指定id的下载
        } else {
            stopPathTask(path);//停止指定路径的下载
        }
    }

    private void stopAllTask(HttpDataGet.Type type) {
        List<OkHttpFileDataGet> list = null;
        for (OkHttpFileDataGet dataGet : mGetS) {
            if (dataGet.getMethod() == type) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(dataGet);
                dataGet.cancel();
                Future c = mCallS.get(dataGet);
                if (c != null) {
                    c.cancel(false);
                }

            }
        }
        if (list != null) {
            removeData(list);
        }
    }

    private void stopIdTask(int id) {
        for (OkHttpFileDataGet dataGet : mGetS) {
            if (dataGet.getInfo().getId() == id) {
                dataGet.cancel();
                Future f = mCallS.get(dataGet);
                if (f != null && !f.isCancelled()) {
                    f.cancel(false);
                }
                removeData(dataGet);
                return;
            }
        }
    }

    private void stopPathTask(String path) {
        for (OkHttpFileDataGet dataGet : mGetS) {
            String infoPath = dataGet.getInfo().getPath() == null ? "" : dataGet.getInfo().getPath();
            String localPath = dataGet.getInfo().getLocalPath() == null ? "" : dataGet.getInfo().getLocalPath();
            if (infoPath.equals(path) || localPath.equals(path)) {
                dataGet.cancel();
                Future f = mCallS.get(dataGet);
                if (f != null && !f.isCancelled()) {
                    f.cancel(false);
                }
                removeData(dataGet);
                return;
            }
        }
    }

    private synchronized void addTask(Intent intent) {
        String localPath = intent.getStringExtra(VALUE_LOCAL_PATH);
        String path = intent.getStringExtra(VALUE_PATH);
        HttpDataGet.Type type = (HttpDataGet.Type) intent.getSerializableExtra(VALUE_TYPE);
        String mark = intent.getStringExtra(VALUE_MARK);
        Object tab = intent.getSerializableExtra(VALUE_TAG);
        DataGet dataGet = null;
        if (type == HttpDataGet.Type.DOWNLOAD) {
            dataGet = AlphaApiService.getInstance().download(mark, path, localPath, tab);
        } else if (type == HttpDataGet.Type.UPLOAD) {
            dataGet = AlphaApiService.getInstance().upload(mark, path, localPath, tab);
        }
        if (dataGet == null) return;
        addActionTask(dataGet);
    }

    private synchronized void addActionTask(DataGet dataGet) {
        OkHttpFileDataGet fileDataGet = (OkHttpFileDataGet) dataGet;
        if (fileDataGet.isComplete()) {
            fileDataGet.handleInfo();
            return;
        }
        for (OkHttpFileDataGet item : mGetS) {
            boolean isAdd = item.judge(fileDataGet);
            if (isAdd) {
                return;
            }
        }
        TaskRunnable runnable = new TaskRunnable(fileDataGet);
        mGetS.add(fileDataGet);
        Future<?> f = null;
        if (fileDataGet.getMethod() == HttpDataGet.Type.DOWNLOAD) {
            f = mDownloadPool.submit(runnable);
        } else if (fileDataGet.getMethod() == HttpDataGet.Type.UPLOAD) {
            f = mUploadPool.submit(runnable);
        }
        if (f != null) {
            mCallS.put(fileDataGet, f);
        }
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "stopService");
        return super.stopService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        HandleMsg.bind(this);
        mDownloadPool = FileActionThreadPool.newThreadPool(3);//创建下载线程池
        mUploadPool = FileActionThreadPool.newThreadPool(3);//创建上传线程池
        mGetS = new Vector<>();
        mCallS = new Hashtable<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        HandleMsg.unbind(this);
        mDownloadPool.shutdown();
    }

    /**
     * 接收在任务完成时发送的消息
     */
    @Mark(OkHttpFileDataGet.TASK_COMPLETE)
    @SelectThread(ThreadMode.ATTACH_THREAD)
    public void $handleDownloadTaskComplete$(OkHttpDownloadDataGet dataGet) {
        //删除保存临时保存的任务
        if (dataGet == null) return;
        removeData(dataGet);
    }

    /**
     * 接收在任务完成时发送的消息
     */
    @Mark(OkHttpFileDataGet.TASK_COMPLETE)
    @SelectThread(ThreadMode.ATTACH_THREAD)
    public void $handleUploadTaskComplete$(OkHttpUploadDataGet dataGet) {
        //删除保存临时保存的任务
        if (dataGet == null) return;
        removeData(dataGet);
    }

    private void removeData(OkHttpFileDataGet dataGet) {
        mGetS.remove(dataGet);
        mCallS.remove(dataGet);
    }

    private void removeData(List<OkHttpFileDataGet> list) {
        if (list == null) return;
        for (OkHttpFileDataGet dataGet : list) {
            mGetS.remove(dataGet);
            mCallS.remove(dataGet);
        }
    }

    class TaskRunnable implements Runnable {

        private OkHttpFileDataGet mDataGet;

        TaskRunnable(OkHttpFileDataGet dataGet) {
            this.mDataGet = dataGet;
        }

        @Override
        public void run() {
            mDataGet.execute();
        }
    }

}
