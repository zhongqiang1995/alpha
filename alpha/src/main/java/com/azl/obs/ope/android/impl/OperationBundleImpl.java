package com.azl.obs.ope.android.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.azl.obs.ope.android.anno.BundleBind;
import com.azl.obs.ope.android.itf.ItfBundleValue;

import java.lang.reflect.Field;

/**
 * Created by zhong on 2017/5/17.
 */

public class OperationBundleImpl implements ItfBundleValue {
    private static ItfBundleValue mInstance;

    public static ItfBundleValue getInstance() {
        if (mInstance == null) {
            synchronized (OperationBundleImpl.class) {
                if (mInstance == null) {
                    mInstance = new OperationBundleImpl();
                }
            }
        }
        return mInstance;
    }
    private OperationBundleImpl(){}
    @Override
    public void bind(Activity activity) {
        bindValue(activity, activity.getIntent().getExtras());
    }

    @Override
    public void bind(Fragment fragment) {
        bindValue(fragment, fragment.getArguments());
    }

    @Override
    public void bind(android.app.Fragment fragment) {
        bindValue(fragment, fragment.getArguments());
    }

    private void bindValue(Object target, Bundle bundle) {
        if (target == null || bundle == null) return;
        Field[] fields = target.getClass().getDeclaredFields();
        Log.i("fields", "fields size:" + fields.length);
        for (Field field : fields) {
            BundleBind annotation = field.getAnnotation(BundleBind.class);
            if (annotation == null) {
                continue;
            }
            String key = annotation.value();
            if (key == null) continue;
            Object value = bundle.get(key);
            if (value == null) continue;
            field.setAccessible(true);
            try {
                field.set(target, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }
}
