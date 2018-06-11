package com.azl.handle.impl;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.azl.handle.bean.Staging;
import com.azl.handle.bean.StagingComparator;

import com.azl.handle.bean.ThreadMode;
import com.azl.handle.itf.ItfHandleAction;
import com.azl.util.CacheThreadPoll;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhong on 2017/5/23.
 */

public class HandleActionImpl implements ItfHandleAction {

    private static HandleActionImpl mInstance;
    private List<Staging.StagingMethod> mDefaultList;
    private Map<String, List<Staging.StagingMethod>> mMarkMap;
    private StagingComparator mComparator;
    private List<Object> mSaveIsRegister;

    private HandleActionImpl() {
        mDefaultList = new ArrayList<>();
        mMarkMap = new HashMap<>();
        mComparator = new StagingComparator();
        mSaveIsRegister = new ArrayList<>();
    }

    public static HandleActionImpl getInstance() {
        if (mInstance == null) {
            synchronized (HandleActionImpl.class) {
                if (mInstance == null) {
                    mInstance = new HandleActionImpl();
                }
            }
        }
        return mInstance;
    }

    @Override
    public synchronized void register(Staging staging) {

        if (staging == null || judgeExists(staging.getTarget())) return;

        mSaveIsRegister.add(staging.getTarget());
        for (int i = 0; i < staging.getMethods().size(); i++) {
            Staging.StagingMethod s = staging.getMethods().get(i);
            if (TextUtils.isEmpty(s.getMark())) {
                mDefaultList.add(0, s);

            } else {
                String mark = s.getMark();
                List<Staging.StagingMethod> list = mMarkMap.get(mark);
                if (list == null) {
                    list = new ArrayList<>();
                    mMarkMap.put(mark, list);
                }
                list.add(0, s);
            }
        }

    }


    @Override
    public synchronized void unregister(Object object) {
        List<Staging.StagingMethod> removeList = null;
        for (Staging.StagingMethod staging : mDefaultList) {
            if (staging.getTarget().equals(object)) {
                if (removeList == null) {
                    removeList = new ArrayList<>();
                }
                removeList.add(staging);
            }
        }
        if (removeList != null) {
            mDefaultList.removeAll(removeList);
        }

        Set<String> keySet = mMarkMap.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            List<Staging.StagingMethod> values = mMarkMap.get(key);
            List<Staging.StagingMethod> removeValues = null;
            if (values != null) {
                for (Staging.StagingMethod staging : values) {
                    if (staging.getTarget().equals(object)) {
                        if (removeValues == null) {
                            removeValues = new ArrayList<>();
                        }
                        removeValues.add(staging);
                    }
                }
            }
            if (removeValues != null) {
                values.removeAll(removeValues);
            }
            if (values != null && values.isEmpty()) {
            }
        }
        mSaveIsRegister.remove(object);

    }

    private boolean judgeExists(Object target) {
        if (target == null || mSaveIsRegister.contains(target)) return true;
        return false;
    }

    @Override
    public void notifyMsg(Object... object) {
        notifyTo(object, mDefaultList);
    }


    @Override
    public void notifyMsgMark(String mark, Object... object) {
        List<Staging.StagingMethod> list = mMarkMap.get(mark);
        notifyTo(object, list);
    }

    private void notifyTo(Object[] object, List<Staging.StagingMethod> list) {
        if (list == null || list.isEmpty()) return;
        sortList(list);
        Class<?>[] classes = null;
        if (object != null && object.length > 0) {
            classes = new Class[object.length];
        }
        for (int i = 0; i < object.length; i++) {
            Object obj = object[i];
            classes[i] = obj.getClass();
        }
        if (classes != null) {
            for (int i = 0; i < classes.length; i++) {
                Class<?> c = classes[i];
                if (c == Integer.class) {
                    classes[i] = int.class;
                } else if (c == Float.class) {
                    classes[i] = float.class;
                } else if (c == Double.class) {
                    classes[i] = double.class;
                } else if (c == Boolean.class) {
                    classes[i] = boolean.class;
                } else if (c == Character.class) {
                    classes[i] = char.class;
                } else if (c == Long.class) {
                    classes[i] = long.class;
                }
            }
        }
        for (Staging.StagingMethod staging : list) {
            try {
                Method method = null;
                Class<?>[] methodParams = staging.getMethod().getParameterTypes();
                int methodParamsSize = methodParams == null ? 0 : methodParams.length;
                int classesSize = classes == null ? 0 : classes.length;
                if (methodParamsSize == classesSize) {

                    if (methodParams == null || classes == null) {
                        method = staging.getMethod();
                    } else {
                        for (int i = 0; i < classes.length; i++) {
                            if (!classes[i].getName().equals(methodParams[i].getName())) {
                                break;
                            }
                            if (i == classes.length - 1) {
                                method = staging.getMethod();
                            }
                        }
                    }
                }
                if (method == null) {
                    continue;
                }
                boolean isIntercept = staging.isIntercept();
                ThreadMode threadMode = staging.getThreadMode();
                if (threadMode == ThreadMode.MAIN_THREAD) {
                    sendMainHandle(object, staging.getTarget(), method);
                } else if (threadMode == ThreadMode.NEW_THREAD) {
                    newThreadExecute(object, staging.getTarget(), method);
                } else {
                    if (object == null || object.length == 0) {
                        method.invoke(staging.getTarget());
                    } else {
                        method.invoke(staging.getTarget(), object);
                    }
                }
                if (isIntercept) {
                    break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void sortList(List<Staging.StagingMethod> list) {
        Collections.sort(list, mComparator);
    }

    public void sendMainHandle(Object[] obj, Object target, Method method) {
        ObjectPackage objectPackage = new ObjectPackage(method, target, obj);
        Message message = new Message();
        message.obj = objectPackage;
        mHandle.sendMessage(message);
    }

    public void newThreadExecute(Object[] obj, Object target, Method method) {
        CacheThreadPoll.getInstance().submit(new ThreadRunnable(method, target, obj));
    }

    Handler mHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ObjectPackage objectPackage = (ObjectPackage) msg.obj;
            Object[] obj = objectPackage.params;
            Object target = objectPackage.target;
            Method method = objectPackage.method;
            try {
                if (obj == null || obj.length == 0) {
                    method.invoke(target);
                } else {
                    method.invoke(target, obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    };


    class ThreadRunnable implements Runnable {

        private Method method;
        private Object target;
        private Object[] params;

        public ThreadRunnable(Method method, Object target, Object[] params) {
            this.method = method;
            this.target = target;
            this.params = params;
        }

        @Override
        public void run() {
            try {
                if (params == null || params.length == 0) {
                    method.invoke(target);
                } else {
                    method.invoke(target, params);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    class ObjectPackage {
        Method method;
        Object target;
        Object[] params;

        public ObjectPackage(Method method, Object target, Object[] params) {
            this.method = method;
            this.target = target;
            this.params = params;
        }
    }
}
