package com.azl.file.helper;

import android.app.Application;
import android.content.Context;

import com.azl.file.bean.Info;
import com.azl.file.db.ImplFileInfoDB;
import com.azl.file.download.helper.DBHelper;
import com.azl.file.download.impl.ImplDownLoad;
import com.azl.file.service.FileActionService;
import com.azl.obs.data.HttpDataGet;

import java.io.File;
import java.io.Serializable;

/**
 * Created by zhong on 2017/6/16.
 */

public class D {
    public static Context APP;

    public static void init(Application app) {
        APP = app;
    }

    public static void download(String targetUrl, String mark) {
        download(targetUrl, mark, null);
    }

    public static void download(String targetUrl, String mark, Serializable tab) {
        download(targetUrl, "", mark, tab);
    }

    public static void upload(String targetUrl, String localPath, String mark, Serializable tab) {
        addTask(localPath, targetUrl, mark, HttpDataGet.Type.UPLOAD, tab);
    }

    public static void stop(int id) {
        stop("", id, null);
    }

    public static void stop(String path) {
        stop(path, -1, null);
    }

    public static void stopAllDownload() {
        stop("", FileActionService.ACTION_REMOVE_ALL_ID, HttpDataGet.Type.DOWNLOAD);
    }


    public static void stopAllUpload() {
        stop("", FileActionService.ACTION_REMOVE_ALL_ID, HttpDataGet.Type.UPLOAD);
    }

    public static Info getDownloadInfo(String path) {
        return DBHelper.getDB(D.APP, ImplDownLoad.TABLE_NAME).getFileInfo(path, HttpDataGet.Type.DOWNLOAD);
    }

    public static Info getUploadInfo(String path) {
        return DBHelper.getDB(D.APP, ImplDownLoad.TABLE_NAME).getFileInfo(path, HttpDataGet.Type.UPLOAD);
    }


    /**
     * 删除已经下载好的文件
     *
     * @param url
     */
    public synchronized static void deleteCompleteFile(String url) {
        if (url == null) return;
        Info info = getDownloadInfo(url);
        if (info != null) {
            File file = info.getCompleteFile();
            if (file != null && file.exists()) {
                file.delete();
            }
            info.setProgress(0);
            info.update(D.APP);
        }
    }

    private static void stop(String path, int id, HttpDataGet.Type type) {
        FileActionService.removeTask(D.APP, id, path, type);
    }


    private static void download(String targetUrl, String localPath, String mark, Serializable tab) {
        addTask(localPath, targetUrl, mark, HttpDataGet.Type.DOWNLOAD, tab);
    }


    private static void addTask(String localPath, String path, String mark, HttpDataGet.Type type, Serializable tab) {
        FileActionService.addTask(D.APP, localPath, path, mark, type, tab);
    }


}
