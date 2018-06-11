package com.azl.obs.data;

import com.azl.file.bean.Info;
import com.azl.obs.retrofit.itf.BaseObserver;
import com.azl.obs.retrofit.itf.FileObserver;
import com.azl.obs.retrofit.itf.Observer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhong on 2017/5/15.
 */

public abstract class HttpDataGet<T> extends DataGet {

    public enum Type {
        GET, POST, UPLOAD, DOWNLOAD
    }

    private Type mMethod = Type.GET;
    private String mUrl = "";
    protected java.lang.reflect.Type mFormatType;
    private Map<String, String> mParams;


    public HttpDataGet(String url, java.lang.reflect.Type c, Type method, Map<String, String> map) {
        this.mUrl = url;
        this.mMethod = method;
        this.mFormatType = c;
        this.mParams = map;
    }

    protected String joinPar(String url, Map<String, String> map) {
        if (map == null) {
            return url;
        }
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (url.contains("?") && url.endsWith("?")) {
                url = url + key + "=" + value;
            } else if (url.contains("?") && !url.endsWith("?")) {
                url = url + "&" + key + "=" + value;
            } else {
                url = url + "?" + key + "=" + value;
            }
        }
        return url;
    }

    @Override
    public abstract void execute(Map<String, String> parMap);

    public void setMethod(Type mMethod) {
        this.mMethod = mMethod;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean updateParams(String key, String value) {
        boolean isExists = false;
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        if (mParams.containsKey(key)) {
            isExists = true;
        }
        mParams.put(key, value);
        return isExists;
    }

    public boolean removeParams(String key, String value) {
        if (mParams == null) return false;
        return mParams.remove(key) != null;
    }

    public void addParams(String key, String value) {
        updateParams(key, value);
    }

    public void setParams(Map<String, String> mPar) {
        this.mParams = mPar;
    }


    public Map<String, String> getParams() {
        return mParams;
    }

    protected java.lang.reflect.Type getFormatClass() {
        return mFormatType;
    }

    public boolean isGet() {
        return mMethod == Type.GET;
    }

    public boolean isPost() {
        return mMethod == Type.POST;
    }

    public boolean isUpload() {
        return mMethod == Type.UPLOAD;
    }

    public boolean isDownload() {
        return mMethod == Type.DOWNLOAD;
    }

    public Type getMethod() {
        return mMethod;
    }

    protected void notifyError(int code, String msg) {
        for (BaseObserver obs : getObs()) {
            if (obs instanceof Observer) {
                ((Observer) obs).onError(code, msg);
            }
        }
    }

    protected void notifyError(int code, T info) {
        for (BaseObserver obs : getObs()) {
            if (obs instanceof BaseObserver) {
                ((FileObserver) obs).onError(code, (Info) info);
            }
        }
    }

}
