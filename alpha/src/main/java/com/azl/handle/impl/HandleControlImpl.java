package com.azl.handle.impl;

import android.text.TextUtils;

import com.azl.handle.anno.Intercept;
import com.azl.handle.anno.Mark;
import com.azl.handle.anno.SelectThread;
import com.azl.handle.bean.Staging;
import com.azl.handle.bean.Tag;
import com.azl.handle.bean.ThreadMode;
import com.azl.handle.itf.ItfControl;
import com.azl.handle.itf.ItfHandleAction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2017/5/23.
 */

public class HandleControlImpl implements ItfControl {
    private ItfHandleAction mHandle;

    private HandleControlImpl(ItfHandleAction handle) {
        this.mHandle = handle;
    }

    public static ItfControl build(ItfHandleAction itfHandle) {
        return new HandleControlImpl(itfHandle);
    }

    @Override
    public void bind(Object target) {
        if (target == null) return;
        Method[] methods = target.getClass().getMethods();
        if (methods == null || methods.length == 0) return;
        List<Staging.StagingMethod> targetMethods = new ArrayList<>();
        for (Method method : methods) {
            if (isTabMethodName(method.getName())) {
                String mark = "";
                ThreadMode threadMode = null;
                int priority = 0;
                boolean isIntercept = false;
                Mark markAnnotation = method.getAnnotation(Mark.class);
                if (markAnnotation != null) {
                    mark = markAnnotation.value();
                    priority = markAnnotation.priority();
                }

                SelectThread selectThreadAnnotation = method.getAnnotation(SelectThread.class);
                if (selectThreadAnnotation != null) {
                    threadMode = selectThreadAnnotation.value();
                }
                Intercept interceptAnnotation = method.getAnnotation(Intercept.class);
                if (interceptAnnotation != null) {
                    isIntercept = interceptAnnotation.value();
                }
                Staging.StagingMethod sm = new Staging.StagingMethod(priority, mark, threadMode, method, target, isIntercept);
                targetMethods.add(sm);
            }
        }
        if (targetMethods != null && !targetMethods.isEmpty()) {
            Staging staging = new Staging(target, targetMethods);
            mHandle.register(staging);
        }
    }

    private boolean isTabMethodName(String methodName) {
        if (TextUtils.isEmpty(methodName)) return false;
        return methodName.startsWith(Tag.METHOD_NAME) || ((methodName.startsWith("$") && methodName.endsWith("$")));
    }

    @Override
    public void unbind(Object object) {
        mHandle.unregister(object);
    }

    @Override
    public void handleMark(String mark, Object... objects) {
        mHandle.notifyMsgMark(mark, objects);
    }

    @Override
    public void handle(Object... object) {
        mHandle.notifyMsg(object);
    }

}
