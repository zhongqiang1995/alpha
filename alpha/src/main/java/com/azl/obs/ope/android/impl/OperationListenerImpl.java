package com.azl.obs.ope.android.impl;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.azl.obs.ope.android.anno.ClickBind;
import com.azl.obs.ope.android.itf.ItfOperationListener;
import com.azl.obs.ope.android.listener.DeclaredOnClickListener;

import java.lang.reflect.Method;

/**
 * Created by zhong on 2017/5/17.
 */

public class OperationListenerImpl implements ItfOperationListener {

    private static ItfOperationListener mInstance;

    public static ItfOperationListener getInstance() {
        if (mInstance == null) {
            synchronized (OperationListenerImpl.class) {
                if (mInstance == null) {
                    mInstance = new OperationListenerImpl();
                }
            }
        }
        return mInstance;
    }

    public OperationListenerImpl() {
    }

    @Override
    public void bind(Object target) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            View contentView = activity.getWindow().getDecorView();
            bind(activity, contentView);
        }
    }

    @Override
    public void bind(Object target, View contentView) {
        if (target == null || contentView == null) return;
        Method[] methods = target.getClass().getDeclaredMethods();
        Log.i("bindMethodLength", "" + (methods == null ? 0 : methods.length));
        for (Method method : methods) {
            ClickBind clickBind = method.getAnnotation(ClickBind.class);
            if (clickBind == null) {
                continue;
            }
            int[] ids = clickBind.value();
            if (ids == null || ids.length == 0) {
                continue;
            }
            for (int id : ids) {
                View targetView = contentView.findViewById(id);
                Log.i("bindMethodName", "" + method.getName());
                targetView.setOnClickListener(new DeclaredOnClickListener(targetView, method.getName(), target));
            }

        }


    }
}
