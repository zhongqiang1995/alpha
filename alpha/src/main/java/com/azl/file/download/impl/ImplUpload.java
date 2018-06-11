package com.azl.file.download.impl;

import android.content.Context;

import com.azl.file.download.itf.ItfDBAction;
import com.azl.file.helper.D;

/**
 * Created by zhong on 2017/11/16.
 */

public class ImplUpload extends ItfDBAction {

    public static final String TABLE_NAME = "uT";
    private static ItfDBAction mInstance;

    public ImplUpload(Context context) {
        super(context);
    }

    public static ItfDBAction getInstance() {
        if (mInstance == null) {
            synchronized (ImplUpload.class) {
                if (mInstance == null) {
                    mInstance = new ImplUpload(D.APP);
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
