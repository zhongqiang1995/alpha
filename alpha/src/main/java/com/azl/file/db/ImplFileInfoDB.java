package com.azl.file.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.azl.file.bean.Info;
import com.azl.file.download.itf.ItfDB;
import com.azl.obs.data.HttpDataGet;

import java.io.File;
import java.util.UUID;

/**
 * Created by zhong on 2017/6/15.
 */

public class ImplFileInfoDB extends ItfDB {


    public ImplFileInfoDB(Context context, String tableName) {
        super(context, tableName);

    }


    @Override
    public synchronized Info getFileInfo(String url, HttpDataGet.Type type) {
        Info info;
        info = getMemoryCache(url);
        if (info != null) {
            return info;
        }
        String select;
        if (type == HttpDataGet.Type.DOWNLOAD) {
            select = "path=?";
        } else {
            select = "localPath=?";
        }
        Cursor cursor = getRDB().query(getTableName(), null, select, new String[]{url}, null, null, null);
        if (cursor != null) {
            boolean isExists = cursor.moveToNext();
            if (isExists) {
                info = getOneInfo(cursor);
                if (info != null) {
                    addToMemoryCache(url, info);
                }
            }
            cursor.close();
        }
        return info;
    }


    @Override
    public synchronized Info newInfo(String path, HttpDataGet.Type type) {
        Info info;

        info = getFileInfo(path, type);
        if (info != null) {
            return info;
        }

        info = new Info();
        File file = new File(path);

        if (type == HttpDataGet.Type.DOWNLOAD) {
            info.setPath(path);
        } else if (type == HttpDataGet.Type.UPLOAD) {
            info.setLocalPath(path);
        }
        info.setCreateTime(System.currentTimeMillis());
        info.setMark(UUID.randomUUID().toString());
        info.setStatus(Info.STATUS_READY);
        info.setLength(file.length());
        ContentValues contentValues = new ContentValues();
        contentValues.put("createTime", info.getCreateTime());
        contentValues.put("path", info.getPath());
        contentValues.put("mark", info.getMark());
        contentValues.put("status", info.getStatus());
        contentValues.put("length", info.getLength());

        long index = getWDB().insert(getTableName(), null, contentValues);
//        if (index == -1) {
//            return null;
//        }
        int id = queryId((int) index);
        info.setId(id);
        boolean isUpdate = updateColumn(new String[]{"_id"}, new String[]{info.getId() + ""}, "mark=?", new String[]{info.getMark()});
//        if (!isUpdate) {
//            return null;
//        }
        addToMemoryCache(path, info);
        return info;
    }

    public void updateMemoryCache(String path, Info info) {
        addToMemoryCache(path, info);
    }

    public Info getMemoryCache(String path) {
        if (path == null) return null;
        Info info = getMemoryCacheInfo().get(path);
        return info;
    }

    public Info getDownloadInfo(String url) {
        return getFileInfo(url, HttpDataGet.Type.DOWNLOAD);
    }

    public Info getUploadInfo(String localPath) {
        return getFileInfo(localPath, HttpDataGet.Type.UPLOAD);
    }


    /**
     * 获取一个下载信息
     *
     * @param cursor
     * @return
     */
    private Info getOneInfo(Cursor cursor) {
        String[] names = cursor.getColumnNames();
        Info info = null;
        if (names != null) {
            info = new Info();
            for (String name : names) {
                int index = cursor.getColumnIndex(name);
                if (name.equals("_id")) {
                    int id = cursor.getInt(index);
                    info.setId(id);
                } else if (name.equals("completeTime")) {
                    long completeTime = cursor.getLong(index);
                    info.setCompleteTime(completeTime);
                } else if (name.equals("createTime")) {
                    long createTime = cursor.getLong(index);
                    info.setCreateTime(createTime);
                } else if (name.equals("length")) {
                    long length = cursor.getLong(index);
                    info.setLength(length);
                } else if (name.equals("progress")) {
                    long progress = cursor.getLong(index);
                    info.setProgress(progress);
                } else if (name.equals("stopCount")) {
                    int stopCount = cursor.getInt(index);
                    info.setStopCount(stopCount);
                } else if (name.equals("contentType")) {
                    String contentType = cursor.getString(index);
                    info.setContentType(contentType);
                } else if (name.equals("path")) {
                    String path = cursor.getString(index);
                    info.setPath(path);
                } else if (name.equals("completePath")) {
                    String completePath = cursor.getString(index);
                    info.setLocalPath(completePath);
                } else if (name.equals("status")) {
                    int status = cursor.getInt(index);
                    info.setStatus(status);
                } else if (name.equals("mark")) {
                    String mark = cursor.getString(index);
                    info.setMark(mark);
                } else if (name.equals("info")) {
                    String text = cursor.getString(index);
                    info.setInfo(text);
                } else if (name.equals("s_data")) {
                    String data = cursor.getString(index);
                    info.setData(data);
                }else if(name.equals("localPath")){
                    String localPath=cursor.getString(index);
                    info.setLocalPath(localPath);
                }
            }
        }
        return info;
    }


}
