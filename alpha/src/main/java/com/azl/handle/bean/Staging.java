package com.azl.handle.bean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhong on 2017/5/23.
 */

public class Staging {
    private Object target;
    private List<StagingMethod> methods;


    public Staging(Object target, int priority, String mark, ThreadMode threadMode, List<StagingMethod> methods) {
        this.methods = methods;
        this.target = target;
    }

    public Staging(Object target, List<StagingMethod> methods) {
        this.target = target;
        this.methods = methods;
    }

    public void setMethods(List<StagingMethod> methods) {
        this.methods = methods;
    }

    public List<StagingMethod> getMethods() {
        return methods;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public static class StagingMethod {
        private int priority;
        private String mark;
        private ThreadMode threadMode;
        private Method method;
        private Object target;
        private boolean isIntercept;

        public StagingMethod(int priority, String mark, ThreadMode threadMode, Method method, Object target, boolean isIntercept) {
            this.isIntercept = isIntercept;
            this.priority = priority;
            this.target = target;
            this.mark = mark;
            this.threadMode = threadMode;
            this.method = method;
        }

        public boolean isIntercept() {
            return isIntercept;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public Object getTarget() {
            return target;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public ThreadMode getThreadMode() {
            return threadMode == null ? ThreadMode.ATTACH_THREAD : threadMode;
        }

        public void setThreadMode(ThreadMode threadMode) {
            this.threadMode = threadMode;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }


    }

}
