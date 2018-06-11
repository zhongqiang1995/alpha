package com.azl.obs.ope.android.listener;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhong on 2017/5/17.
 * 从安卓源码中拿的类，修改了一下
 */

public class DeclaredOnClickListener implements View.OnClickListener {
    private final View mHostView;
    private final String mMethodName;

    private Method mResolvedMethod;
    private Context mResolvedContext;
    private Object mObj;

    public DeclaredOnClickListener(@NonNull View hostView, @NonNull String methodName, Object obj) {
        mHostView = hostView;
        mMethodName = methodName;
        this.mObj = obj;
    }

    public DeclaredOnClickListener(@NonNull View hostView, @NonNull String methodName) {
        mHostView = hostView;
        mMethodName = methodName;
    }

    @Override
    public void onClick(@NonNull View v) {
        if (mResolvedMethod == null) {
            if(mObj==null){
                resolveMethod(mHostView.getContext(), mMethodName);
            }else{
                resolveMethod(mObj, mMethodName);
            }
        }

        try {
            Class<?>[] params = mResolvedMethod.getParameterTypes();
            if (params == null || params.length == 0) {
                mResolvedMethod.invoke(mObj==null?mResolvedContext:mObj);
            } else {
                mResolvedMethod.invoke(mObj==null?mResolvedContext:mObj, v);
            }

        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "Could not execute non-public method for android:onClick", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "Could not execute method for android:onClick", e);
        }
    }

    @NonNull
    private void resolveMethod(@Nullable Object obj, @NonNull String name) {
        while (obj != null) {
            try {
                Method method = null;
                try {
                    method = obj.getClass().getMethod(mMethodName, View.class);
                } catch (Exception e) {
                }
                if (method == null) {
                    method = obj.getClass().getMethod(mMethodName);
                }
                if (method != null) {
                    mResolvedMethod = method;
                    return;
                }
            } catch (Exception e) {
                // Failed to find method, keep searching up the hierarchy.
            }
        }

        final int id = mHostView.getId();
        final String idText = id == View.NO_ID ? "" : " with id '"
                + mHostView.getContext().getResources().getResourceEntryName(id) + "'";
        throw new IllegalStateException("Could not find method " + mMethodName
                + "(View) in a parent or ancestor Context for android:onClick "
                + "attribute defined on view " + mHostView.getClass() + idText);
    }

    @NonNull
    private void resolveMethod(@Nullable Context context, @NonNull String name) {
        while (context != null) {
            try {
                if (!context.isRestricted()) {
                    Method method = null;
                    try {
                        method = context.getClass().getMethod(mMethodName, View.class);
                    } catch (Exception e) {
                    }
                    if (method == null) {
                        method = context.getClass().getMethod(mMethodName);
                    }
                    if (method != null) {
                        mResolvedMethod = method;
                        mResolvedContext = context;
                        return;
                    }
                }
            } catch (Exception e) {
                // Failed to find method, keep searching up the hierarchy.
            }

            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                // Can't search up the hierarchy, null out and fail.
                context = null;
            }
        }

        final int id = mHostView.getId();
        final String idText = id == View.NO_ID ? "" : " with id '"
                + mHostView.getContext().getResources().getResourceEntryName(id) + "'";
        throw new IllegalStateException("Could not find method " + mMethodName
                + "(View) in a parent or ancestor Context for android:onClick "
                + "attribute defined on view " + mHostView.getClass() + idText);
    }
}
