package com.azl.file.download.impl;

import android.content.Context;

import com.azl.file.download.itf.ItfDBAction;
import com.azl.file.helper.D;

/**
 * Created by zhong on 2017/7/2.
 */

public class ImplDownLoad extends ItfDBAction {

    public static final String TABLE_NAME = "dT";
    private static ItfDBAction mInstance;

    public ImplDownLoad(Context context) {
        super(context);
    }

    public static ItfDBAction getInstance() {
        if (mInstance == null) {
            synchronized (ImplDownLoad.class) {
                if (mInstance == null) {
                    mInstance = new ImplDownLoad(D.APP);
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }



}
