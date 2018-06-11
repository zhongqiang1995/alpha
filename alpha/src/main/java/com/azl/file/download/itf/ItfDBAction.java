package com.azl.file.download.itf;

import android.content.Context;

import com.azl.file.bean.Info;
import com.azl.file.download.helper.DBHelper;
import com.azl.obs.data.HttpDataGet;

/**
 * Created by zhong on 2017/6/14.
 */

public abstract class ItfDBAction {
    private ItfDB mDB;

    public ItfDBAction(Context context) {
        mDB = DBHelper.getDB(context, getTableName());
    }

    public Info task(String targetPath, HttpDataGet.Type type) {
        Info info = mDB.newInfo(targetPath, type);
        return info;
    }

    public Info already(String targetPath, HttpDataGet.Type type) {
        Info info = mDB.getFileInfo(targetPath, type);
        return info;
    }

    public boolean remove(String targetPath, boolean isRemoveDB) {
        return mDB.remove(targetPath, isRemoveDB);
    }

    public abstract String getTableName();


}
