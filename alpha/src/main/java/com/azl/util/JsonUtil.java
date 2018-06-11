package com.azl.util;

import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhongq on 2016/9/22.
 */
public class JsonUtil {
    private static final String TAG="JsonUtil";
    private static JsonUtil jsonUtil;
    private static Gson gson;
    private static LruCache<String,Object> cacheMap;
    private static MessageDigest md5;

    public synchronized static JsonUtil getInstance(){
        if(jsonUtil==null){
            jsonUtil=new JsonUtil();
            gson=new Gson();
            cacheMap=new LruCache<>(100000);
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return jsonUtil;
    }
    private JsonUtil(){}


    /**
     * 获取指定路径下的对象
     * @param json
     * @param route
     * @param c
     * @param <T>
     * @return
     */
    public <T> T fromJson(String json, String route, Class<T> c){
        Object obj = getObject(json, route);
        if(obj==null)return null;
        return gson.fromJson(gson.toJson(obj),c);
    }
    public <T> T fromJson(String json, String route,  Type typeOfT){
        Object obj = getObject(json, route);
        if(obj==null)return null;
        return gson.fromJson(gson.toJson(obj),typeOfT);
    }

    /**
     * 把字符串转换为相应的对象
     * @param json
     * @param c
     * @param <T>
     * @return
     */
    public <T> T fromJson(String json, Class<T> c){
        return gson.fromJson(json,c);
    }

    /**
     * 将对象转换为字符串
     * @param c
     * @return
     */
    public String toJson(Class c){
        return gson.toJson(c);
    }

    /**
     * 获取route路径下的对象
     * @param json
     * @param route
     * @return
     */
    public Object getObject(String json, String route){
        return formatData(json, route);
    }

    /**
     * 判断route路径下的对象是否为空
     * @param json
     * @param route
     * @return
     */
    public boolean isNull(String json, String route){
        return getObject(json,route)==null;
    }

    /**
     * 获取map或者list对象的长度，如果route路径的对象为null默认返回0
     * @param json
     * @param route
     * @return
     */
    public int getCollectionSize(String json, String route){
        Object obj=getObject(json,route);
        try {
            if(obj==null){
                throw new Exception("the collection not found :"+route);
            }
            if(obj instanceof Map){
                return   ((Map)obj).size();
            }else if(obj instanceof List){
                return  ((List)obj).size();
            }else{
                throw new Exception("is not a collection :"+route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return 0;
        }
    }

    /**
     *
     * @param json
     * @param route
     * @return
     */
    @Nullable
    private Object formatData(String json, String route) {
        Object obj=fromJsonObject(json);
        if(obj==null)return null;
        String[] arr= route.split("/");
        for(int i=0;i<arr.length;i++){
            if(obj instanceof Map){
                obj= getMapValue((Map<String, Object>) obj,arr[i]);
            }else if(obj instanceof List){
                String value=arr[i];
                boolean is = isArr(value);
                if(!is){
                    return null;
                }

                obj= getListValue((List<Object>) obj, getArrIndex(value));
            }else if(obj==null){
                return null;
            }
        }
        return obj;
    }
    static Pattern pattern;
    protected static boolean isArr(String value) {
        if(pattern==null){
            pattern= Pattern.compile("\\[\\d+\\]");
        }
        Matcher matcher=pattern.matcher(value);
        return matcher.matches();
    }

    protected static int getArrIndex(String value){
        int start=value.indexOf("[");
        int end=value.indexOf("]");
        return Integer.valueOf(value.substring(start+1,end));
    }
    /**
     *
     * @param json
     * @return
     */
    private Object fromJsonObject(String json) {
        String md5=getMd5(json);
        Object obj=cacheMap.get(md5);
        if(obj!=null){
            Log.d(TAG,"gets the cache object");
            return obj;
        }
        obj= gson.fromJson(json.toString(),new TypeToken<HashMap<String,Object>>(){}.getRawType());
        cacheMap.put(md5,obj);
        Log.d(TAG,"get a new object");
        return obj;
    }

    /**
     * 计算字符串的MD5
     * @param str
     * @return
     */
    public static String getMd5(String str) {
        byte[] bs = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x:bs) {
            if((x & 0xff)>>4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }

    /**
     * 获取在map中的值
     * @param map
     * @param key
     * @return
     */
    private Object getMapValue(Map<String,Object> map, String key){
        if(!map.containsKey(key)){
            return null;
        }
        return map.get(key);
    }

    /**
     * 获取的list中的值
     * @param list
     * @param index 下标
     * @return
     */
    private Object getListValue(List<Object> list, int index){
        if(list==null||list.size()<=index){
            return null;
        }
        return list.get(index);
    }

}
