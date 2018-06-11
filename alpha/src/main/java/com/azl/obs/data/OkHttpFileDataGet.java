package com.azl.obs.data;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.azl.file.bean.Info;
import com.azl.file.download.impl.ImplDownLoad;
import com.azl.file.download.impl.ImplUpload;
import com.azl.file.download.itf.ItfDBAction;
import com.azl.file.helper.D;
import com.azl.handle.action.HandleMsg;
import com.azl.util.OkHttpHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by zhong on 2017/6/15.
 */

public abstract class OkHttpFileDataGet extends HttpDataGet<Info> {

    /**
     * 任务完成时会发送改消息，不管成功还是失败
     *
     * @params com.azl.obs.data.OkHttpFileDataGet
     */
    public static final String TASK_COMPLETE = "OkHttpFileDataGetTaskComplete";

    private Info mInfo;
    protected String TAG = "";
    private Call mCall;
    private OkHttpClient client;
    private ItfDBAction mDB;
    private String mLocalPath;//还没用到
    private List<String> mMarks;
    private boolean mIsRun;
    private long mNotifyLastTime;//记录最后一次通知的时间
    private Object mTab;

    public OkHttpFileDataGet(String path, java.lang.reflect.Type type, Type method, Map<String, String> map, String localPath, String mark, Object tab) {
        super(path, type, method, map);
        this.client = OkHttpHelper.getClient();
        this.TAG = this.getClass().getSimpleName();
        this.mDB = ImplDownLoad.getInstance();
        this.mInfo = mDB.task(method == Type.UPLOAD ? localPath : path, method);
        if (method == Type.UPLOAD) {
            this.mInfo.setFlag(Info.FLAG_UPLOAD);
        } else if (method == Type.DOWNLOAD) {
            this.mInfo.setFlag(Info.FLAG_DOWNLOAD);
        }
        this.mMarks = new ArrayList<>();
        this.mLocalPath = localPath;
        this.mMarks.add(mark);
        this.mTab = tab;


    }


    public void addMark(List<String> mark) {
        if (mark == null) return;
        for (String m : mark) {
            if (!mMarks.contains(m)) {
                mMarks.add(m);
            }
        }
        if (!getInfo().isProgress()) {
            handleInfo(getInfo(), mark);
        }
    }


    @Override
    public void execute() {
        execute(getParams());
    }

    @Override
    public synchronized void execute(Map<String, String> parMap) {
        if (isRun()) return;
        if (infoIsNullAction(getInfo())) {
            return;
        }
        String url = joinPar(getUrl(), parMap);
        updateRunStatus(true);
        doAction(url);
        updateRunStatus(false);
        updateInfoToDB();
        handleInfo(getInfo(), mMarks);
        HandleMsg.handleMark(TASK_COMPLETE, this);
    }

    protected boolean infoIsNullAction(Info info) {
        if (info == null) {
            info.setPath(getUrl());
            info.setStatus(Info.STATUS_ERROR);
            info.setInfo("操作失败");
            handleInfo(info, getMarks());
            return true;
        }
        return false;
    }

    /**
     * 更新信息到数据库中
     */
    protected void updateInfoToDB() {
        if (getInfo() != null) {
            getInfo().update(D.APP);
        }
    }

    /**
     * 更新实时的状态
     *
     * @param isRun
     */
    private void updateRunStatus(boolean isRun) {
        mIsRun = isRun;
        mInfo.setRun(isRun);
    }

    /**
     * 判断传入的任务是否已经开始，通过path是否相同来判断
     *
     * @return true相同反之
     */
    public boolean judge(OkHttpFileDataGet dataGet) {
        if (dataGet.getMethod() == Type.DOWNLOAD) {
            if (dataGet.getUrl().equals(getUrl())) {
                addMark(dataGet.getMarks());
                return true;
            }
        } else if (dataGet.getMethod() == Type.UPLOAD) {
            if (dataGet.getLocalPath().equals(getLocalPath())) {
                addMark(dataGet.getMarks());
                return true;
            }
        }

        return false;
    }

    protected void handleInfo(Info info, List<String> marks) {
        if (info == null) return;
        if (System.currentTimeMillis() - mNotifyLastTime > getNotifyIntervalTime() || !info.isProgress()) {
            mNotifyLastTime = System.currentTimeMillis();
            for (String mark : marks) {
                Info info1 = info.copy();
                info1.setTab(mTab);
                HandleMsg.handleMark(mark, info1);
            }
            info.setSpeed(0);
        }
    }

    public void handleInfo() {
        handleInfo(getInfo(), getMarks());
    }


    public boolean isRun() {
        return mIsRun;
    }

    @Override
    public void cancel() {
        pause();
    }

    private void pause() {
  
        if (getInfo() != null) {
            getInfo().setInfo("暂停");
            getInfo().setStopCount(getInfo().getStopCount() + 1);
            getInfo().setStatus(Info.STATUS_PAUSE);
        }
        if (!isRun()) {
            handleInfo(getInfo(), getMarks());
        }
        updateInfoToDB();
        mIsRun = false;
        if (mCall != null && mCall.isExecuted()) {
            mCall.cancel();
        }

    }

    public boolean isComplete() {
        if (getMethod() == Type.UPLOAD) {
            return mInfo.isComplete() && getUrl().equals(getInfo().getPath());
        }
        return mInfo.isComplete();
    }

    /**
     * @return 返回通知间隔的时间
     */
    protected long getNotifyIntervalTime() {
        return 1000;
    }


    protected List<String> getMarks() {
        return mMarks;
    }

    public String getLocalPath() {
        return mLocalPath;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    protected long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setCall(Call mCall) {
        this.mCall = mCall;
    }

    public Info getInfo() {
        return mInfo;
    }

    protected abstract void doAction(String url);

    public Object getTab() {
        return mTab;
    }
}
