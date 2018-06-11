package com.azl.obs.ope.android.impl;

import android.app.Activity;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.view.View;

import com.azl.obs.ope.android.anno.ViewBind;
import com.azl.obs.ope.android.itf.ItfOperationId;
import com.azl.obs.ope.android.listener.DeclaredOnClickListener;

import java.lang.reflect.Field;

/**
 * Created by zhong on 2017/5/17.
 */

public class OperationIdImpl implements ItfOperationId {

    private static ItfOperationId mInstance;

    public static ItfOperationId getInstance() {
        if (mInstance == null) {
            synchronized (OperationIdImpl.class) {
                if (mInstance == null) {
                    mInstance = new OperationIdImpl();
                }
            }
        }
        return mInstance;
    }

    private OperationIdImpl(){}
    @Override
    public void bind(Object object, View tartView) {
        if (object == null || tartView == null) return;
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            ViewBind annotation = field.getAnnotation(ViewBind.class);
            if (annotation == null) {
                continue;
            }
            field.setAccessible(true);
            String clickMethodName = annotation.click();
            int viewId = annotation.value();
            View view = tartView.findViewById(viewId);
            try {
                field.set(object, view);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            addClickListener(object, field, clickMethodName);
        }
    }

    private void addClickListener(Object object, Field field, String clickMethodName) {
        if (!TextUtils.isEmpty(clickMethodName)) {
            try {
                View view = (View) field.get(object);
                if(object instanceof ContextWrapper){
                    view.setOnClickListener(new DeclaredOnClickListener(view, clickMethodName));
                }else{
                    view.setOnClickListener(new DeclaredOnClickListener(view, clickMethodName,object));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void bind(Activity activity) {
        View view = activity.getWindow().getDecorView();
        bind(activity, view);
    }

}
