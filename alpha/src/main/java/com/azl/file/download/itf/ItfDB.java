package com.azl.file.download.itf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.azl.file.bean.Info;
import com.azl.obs.data.HttpDataGet;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by zhong on 2017/6/14.
 */

public abstract class ItfDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "ulDB";
    public static final int DB_VERSION = 6;
    private SQLiteDatabase mWDB;
    private SQLiteDatabase mRDB;
    private Map<String, Info> mMemoryCacheInfo;
    private String mTableName;

    public ItfDB(Context context, String tableName) {

        this(context, DB_NAME, null, DB_VERSION, null, tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    public ItfDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler, String tableName) {
        super(context, name, factory, version, errorHandler != null ? errorHandler : new DefaultDatabaseErrorHandler());
        this.mTableName = tableName;
        init();
    }

    protected void init() {
        mMemoryCacheInfo = new Hashtable<>();
        mRDB = getReadableDatabase();
        mWDB = getWritableDatabase();
    }


    protected void createDB(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + getTableName() +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "completeTime INT(64) default 0," +
                "createTime INT(64) default 0," +
                "length INT(64) DEFAULT 0," +
                "progress INT(64) DEFAULT 0," +
                "stopCount INT DEFAULT 0," +
                "contentType CHAR(32)," +
                "path CHAR(32)," +
                "info CHAR(32)," +
                "localPath CHAR(32)," +
                "status int DEFAULT 0," +
                "mark CHAR(32) NOT NULL," +
                "s_data text)");
    }


    public boolean update(ContentValues values, String where, String[] whereValue) {
        int index = 0;
        try {
            index = getWDB().update(getTableName(), values, where, whereValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index > 0;
    }

    public boolean update(Info info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("localPath", info.getLocalPath());
        contentValues.put("completeTime", info.getCompleteTime());
        contentValues.put("contentType", info.getContentType());
        contentValues.put("createTime", info.getCreateTime());
        contentValues.put("length", info.getLength());
        contentValues.put("path", info.getPath());
        contentValues.put("progress", info.getProgress());
        contentValues.put("status", info.getStatus());
        contentValues.put("stopCount", info.getStopCount());
        contentValues.put("s_data", info.getData());
        contentValues.put("info", info.getInfo());
        int index = mWDB.update(getTableName(), contentValues, "_id=?", new String[]{info.getId() + ""});
        return index > 0;
    }

    public boolean updateStatus(Info info, int status) {
        return updateColumn(new String[]{"status"}, new String[]{status + ""}, "_id=?", new String[]{info.getId() + ""});
    }


    protected Info getMemoryCache(String url) {
        return mMemoryCacheInfo.get(url);
    }

    protected void addToMemoryCache(String url, Info info) {
        mMemoryCacheInfo.put(url, info);
    }

    public Map<String, Info> getMemoryCacheInfo() {
        return mMemoryCacheInfo;
    }

    protected int queryId(int row) {
        int id = -1;
        Cursor cursor = getRDB().rawQuery("SELECT * FROM " + getTableName() + " LIMIT " + (row - 1) + ",1;", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex("_id");
                id = cursor.getInt(index);
            }
            cursor.close();
        }
        return id;
    }

    public boolean updateColumn(String[] columnName, String[] value, String where, String[] whereValue) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < columnName.length; i++) {
            values.put(columnName[i], value[i]);
        }
        return update(values, where, whereValue);
    }

    public boolean remove(String targetPath, boolean isRemoveDB) {
        boolean is = false;
        if (isRemoveDB) {
            int index = getWDB().delete(getTableName(), "path=?", new String[]{targetPath});
            is = index > 0;
        }
        Info info = mMemoryCacheInfo.remove(targetPath);
        if (!is) {
            is = info != null;
        }

        return is;
    }

    public boolean remove(int id, boolean isRemoveDB) {
        return false;
    }


    public abstract Info getFileInfo(String url, HttpDataGet.Type type);

    public abstract Info newInfo(String path, HttpDataGet.Type type);

    protected String getTableName() {
        return mTableName;
    }

    public SQLiteDatabase getRDB() {
        return mRDB;
    }

    public SQLiteDatabase getWDB() {
        return mWDB;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE " + getTableName());
            createDB(db);
        }
    }

    ;

}
