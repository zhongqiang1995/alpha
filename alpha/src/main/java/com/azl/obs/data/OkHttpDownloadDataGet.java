package com.azl.obs.data;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.azl.bean.PermissionPackageBean;
import com.azl.file.bean.Info;
import com.azl.file.helper.D;
import com.azl.handle.action.HandleMsg;
import com.azl.helper.AKXMarkList;
import com.azl.obs.exception.DownloadException;
import com.azl.util.StreamHelper;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhong on 2017/7/7.
 */

public class OkHttpDownloadDataGet extends OkHttpFileDataGet {
    public OkHttpDownloadDataGet(String path, java.lang.reflect.Type type, Type method, Map<String, String> map, String localPath, String mark, Object tab) {
        super(path, type, method, map, localPath, mark, tab);
        statusAction();
    }


    private void statusAction() {
        if (infoIsNullAction(getInfo())) return;
        getInfo().setProgress(getInfo().getCompleteFile() == null ? 0 : getInfo().getCompleteFile().length());
        if (!getInfo().isComplete()) {
            getInfo().setProgress(getInfo().getCompleteFile() == null ? 0 : getInfo().getCompleteFile().length());
            if (getInfo().getLength() != getInfo().getProgress()) {
                updateInfoToDB();
            }
        } else {
            getInfo().setInfo("完成");
        }
        if (!getInfo().isComplete() && !getInfo().isRun()) {
            getInfo().setStatus(Info.STATUS_QUEUE);
            getInfo().setInfo("队列中");
        }

        handleInfo(getInfo(), getMarks());
    }




    protected void doAction(String url) {
        Info info = getInfo();

        RandomAccessFile aF = null;
        InputStream input = null;
        long downloadLength = 0;
        if (info.isComplete()) {
            if (judgeFileLegal(info)) return;
        }
        info.setStatus(Info.STATUS_READY);
        info.setInfo("下载准备中");

        if (info.getCompleteFile() != null) {
            File downLoadFile = info.getCompleteFile();
            downloadLength = downLoadFile.length();
            info.setProgress(downloadLength);
        }
        handleInfo(info, getMarks());
        Request request = new Request.Builder().addHeader("RANGE", "bytes=" + downloadLength + "-").url(url).build();
        Call call = getClient().newCall(request);
        setCall(call);

        try {
            if (!isSdPermissions()) {
                //判断有无sd卡的权限
                if (!isSdPermissions()) {
                    HandleMsg.handleMark(AKXMarkList.MARK_PERMISSIONS, new PermissionPackageBean(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}));
                    throw new DownloadException("权限不足");
                }
            }
            if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                info.setInfo("内存卡不存在");
                throw new DownloadException("Memory card does not exist");
            }
            if (info.getLength() != 0 && (info.getLength() - info.getProgress()) > getSDAvailableSize()) {
                info.setInfo("储存不足");
                throw new DownloadException("Insufficient storage");
            }
            Response response = call.execute();
            byte[] buf = new byte[1024 * 1024];

            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kuxiao";

            File fileDir = new File(sdPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            input = response.body().byteStream();
            long total = response.body().contentLength();

            if (total > getSDAvailableSize()) {
                info.setInfo("储存不足");
                throw new DownloadException("Insufficient storage");
            }
            if (downloadLength == 0) {
                info.setLength(total);//获取总进度
            }
            File file = new File(sdPath, info.getMark());
            info.setLocalPath(file.getAbsolutePath());
            if (!file.exists()) {
                file.createNewFile();
            }
            aF = new RandomAccessFile(file, "rw");
            aF.seek(downloadLength);
            int len;

            info.setStatus(Info.STATUS_PROGRESS);
            info.setInfo("下载中");
            updateInfoToDB();
            while (isRun() && (len = input.read(buf)) != -1) {
                info.setSpeed(info.getSpeed() + len);
                aF.write(buf, 0, len);
                info.setProgress(info.getProgress() + len);
//                Log.d(TAG, "total=" + info.getLength() + "  progress:" + info.getProgress() + " status:" + info.getStatus());
                handleInfo(info, getMarks());
            }

            if (info.getProgress() == info.getLength()) {
                info.setStatus(Info.STATUS_COMPLETE);
                info.setInfo("完成");
                info.resetCompleteName(info);
                updateInfoToDB();
            } else {
                info.setStatus(Info.STATUS_PAUSE);
                info.setInfo("下载暂停");
            }

        } catch (Exception e) {
            if (isRun()) {
                if (!(e instanceof DownloadException)) {
                    info.setInfo("下载失败");
                }
                info.setStatus(Info.STATUS_ERROR);
                Log.d(TAG, "download pause error");
                e.printStackTrace();
            } else {
                Log.d(TAG, "download pause");
            }
        } finally {
            StreamHelper.close(input, aF);
        }
    }

    /**
     * 判断有无sd卡权限
     *
     * @return
     */
    private boolean isSdPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(D.APP, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;

    }


    /**
     * 判断文件是否合法
     *
     * @param info
     * @return
     */
    private boolean judgeFileLegal(Info info) {
        if (!TextUtils.isEmpty(info.getLocalPath())) {
            File completeFile = new File(info.getLocalPath());
            if (completeFile.exists()) {
                if (info.getLength() == info.getProgress()) {
                    return true;
                } else {
                    completeFile.delete();
                }
            }
        }
        info.resetDownloadVar();
        return false;
    }


}
