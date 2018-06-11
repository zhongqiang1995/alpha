package com.azl.obs.retrofit.handle;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.azl.obs.data.OkHttpDownloadDataGet;
import com.azl.obs.data.OkHttpUploadDataGet;
import com.azl.obs.retrofit.anno.DefaultParam;
import com.azl.obs.retrofit.anno.Download;
import com.azl.obs.retrofit.anno.Get;
import com.azl.obs.retrofit.anno.HandleMark;
import com.azl.obs.retrofit.anno.JsonBody;
import com.azl.obs.retrofit.anno.LocalPath;
import com.azl.obs.retrofit.anno.Param;
import com.azl.obs.retrofit.anno.Paths;
import com.azl.obs.retrofit.anno.Post;
import com.azl.obs.retrofit.anno.Tag;
import com.azl.obs.retrofit.anno.Upload;
import com.azl.obs.retrofit.anno.Url;
import com.azl.obs.data.DataGet;
import com.azl.obs.data.HttpDataGet;
import com.azl.obs.data.OkHttpDataGet;
import com.azl.obs.retrofit.helper.ConstructionImpl;
import com.azl.util.GsonUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhong on 2017/5/16.
 */

public class ObsHandle implements InvocationHandler {
    private static final String TAG = "ObsHandle";
    private ConstructionImpl.Build mBuild;

    public ObsHandle(ConstructionImpl.Build build) {
        this.mBuild = build;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpDataGet.Type methodType = null;
        String url = "";
        String localPath;
        String mark;
        List<String> bodyJsonList;
        Object tab;

        Get get = method.getAnnotation(Get.class);
        if (get != null) {
            methodType = HttpDataGet.Type.GET;
            url = get.value();
        }
        Post post = method.getAnnotation(Post.class);
        if (post != null) {
            methodType = HttpDataGet.Type.POST;
            url = post.value();
        }
        Download download = method.getAnnotation(Download.class);
        if (download != null) {
            url = download.url();
            methodType = HttpDataGet.Type.DOWNLOAD;
        }

        Upload upload = method.getAnnotation(Upload.class);
        if (upload != null) {
            url = upload.url();
            methodType = HttpDataGet.Type.UPLOAD;
        }


        localPath = getParamsValue(args, method, LocalPath.class);//获取方法中的本地地址
        mark = getParamsValue(args, method, HandleMark.class);//获取方法中的mark
        tab = getParamsValue(args, method, Tag.class);
        String paramsUrl = getParamsValue(args, method, Url.class);//获取参数中的url

        bodyJsonList = getParamsBodyJson(args, method);//获取请求体内容
        Map<String, String> params = accessParams(args, method);//获取请求参数，没有参数则为空

        url = (paramsUrl != null && !paramsUrl.equals("")) ? paramsUrl : url; //参数中有标示注解URL注解的优先选择
        url = judgeParams(url); //判断是否有需要替换的字符串
        url = judgingLawfulUrl(url); //判断url是否合法


        if (methodType == HttpDataGet.Type.POST && url.contains("?")) {
            String[] arr = url.split("\\?");
            if (arr != null && arr.length == 2) {
                url = arr[0];
                params = splitPostParams(arr[1], params);
            }
        }
        //添加默认属性
        boolean isAddDefault = isDefaultParams(method);
        if (isAddDefault) {
            params = addDefaultParamMap(params);
        }
        Type format = null;
        //获取返回值类型
        Type returnValueType = method.getGenericReturnType();
        if (returnValueType == null) return null;
        if (returnValueType instanceof ParameterizedType) {
            //是泛型
            Type[] types = ((ParameterizedType) returnValueType).getActualTypeArguments();
            for (Type t : types) {
                format = t;
                break;
            }
        }
        DataGet dataGet = null;
        if (methodType == HttpDataGet.Type.GET || methodType == HttpDataGet.Type.POST) {
            dataGet = new OkHttpDataGet(url, format, methodType, params, bodyJsonList);
        } else if (methodType == HttpDataGet.Type.DOWNLOAD) {
            dataGet = new OkHttpDownloadDataGet(url, format, methodType, params, localPath, mark, tab);
        } else if (methodType == HttpDataGet.Type.UPLOAD) {
            dataGet = new OkHttpUploadDataGet(url, format, methodType, params, localPath, mark, tab);
        }
        return dataGet;
    }


    private Map<String, String> splitPostParams(String s, Map<String, String> params) {
        if (TextUtils.isEmpty(s)) return params;
        if (params == null) {
            params = new HashMap<>();
        }
        String[] arr = s.split("\\&");
        for (String a : arr) {
            String[] ss = a.split("\\=");
            if (ss != null && ss.length == 2) {
                params.put(ss[0], ss[1]);
            }
        }
        return params;
    }

    private boolean isDefaultParams(Method method) {
        DefaultParam param = method.getAnnotation(DefaultParam.class);
        if (param == null) {
            return true;
        }
        return param.value();
    }

    private Map<String, String> addDefaultParamMap(Map<String, String> params) {
        if (mBuild.getDefaultParams() != null && !mBuild.getDefaultParams().isEmpty()) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.putAll(mBuild.getDefaultParams());
        }
        return params;
    }


    private <T> T getParamsValue(Object[] args, Method method, Class c) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] annotation = annotations[i];
            for (int j = 0; j < annotation.length; j++) {
                Annotation anno = annotation[j];
                if (anno.annotationType().equals(c)) {
                    T mark = (T) args[i];
                    return mark;
                }
            }
        }
        return null;
    }


    /**
     * 获取参数中的url
     *
     * @return value
     */
    private List<String> getParamsBodyJson(Object[] args, Method method) {
        List<String> list = null;
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] annotation = annotations[i];
            for (int j = 0; j < annotation.length; j++) {
                Annotation anno = annotation[j];
                if (anno instanceof JsonBody) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    Object body = args[i];
                    String json = "";
                    if (body instanceof String) {
                        json = (String) body;
                    } else {
                        json = GsonUtil.toJson(body);
                    }
                    if (json != null) {
                        list.add(json);
                    }
                }
            }
        }
        return list;
    }


    @Nullable
    private Map<String, String> accessParams(Object[] args, Method method) {
        Map<String, String> map = null;
        Annotation[][] annotations = method.getParameterAnnotations();
        if (annotations == null) return null;
        for (int i = 0; i < annotations.length; i++) {
            if (map == null) {
                map = new HashMap<>();
            }
            Annotation[] par = annotations[i];
            for (int j = 0; j < par.length; j++) {
                Annotation ano = par[j];
                if (ano instanceof Param) {
                    Param p = (Param) ano;
                    map.put(p.value(), (String) args[i]);
                }
            }
        }
        return map;
    }

    private String judgingLawfulUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        boolean isHttpStart = url.startsWith("http://") || url.startsWith("https://");
        if (!isHttpStart) {
            url = "http://" + url;
        }
        return url;
    }

    private static final String PATTEN = "\\{.*\\}";

    private String judgeParams(String url) {
        if (mBuild.getParams() != null) {
            Pattern pat = Pattern.compile(PATTEN);
            Matcher mat = pat.matcher(url);
            boolean isExists = mat.find();
            if (isExists) {
                String group = mat.group();
                String[] arr = group.split("[‘{’,‘}’,'']");
                for (String key : arr) {
                    if (key == null || key.equals("")) continue;
                    String value = mBuild.getParams().get(key);
                    if (value == null) continue;
                    url = url.replace("{" + key + "}", value);
                }
            }
        }
        Log.e("value", "value:" + url);


        return url;
    }

    private String addDefaultParams(String url) {
        if (mBuild.getDefaultParams() == null) {
            return url;
        }
        Set<String> keySet = mBuild.getDefaultParams().keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = mBuild.getDefaultParams().get(key);
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
}
