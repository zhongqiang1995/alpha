package com.azl.file.download.helper;

import android.content.Context;

import com.azl.file.db.ImplFileInfoDB;

import java.util.HashMap;

/**
 * Created by zhong on 2017/7/6.
 */

public class DBHelper {
    private static HashMap<String, ImplFileInfoDB> mDBMap;

    static {
        mDBMap = new HashMap<>();
    }


    public synchronized static ImplFileInfoDB getDB(Context context, String tableName) {
        ImplFileInfoDB db = mDBMap.get(tableName);
        if (db == null) {
            db = new ImplFileInfoDB(context, tableName);
            mDBMap.put(tableName, db);
        }
        return db;

    }

}
