package com.azl.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GsonUtil {
    private static Gson mGson = new Gson();

    public static Gson getInstance() {
        return mGson;
    }

    private GsonUtil() {
    }

    public static String toJson(Object src) {
        return getInstance().toJson(src);
    }

    public static String toJson(Object src, Type typeOfT) {
        return getInstance().toJson(src, typeOfT);

    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getInstance().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {

        return getInstance().fromJson(json, typeToken.getType());

    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return getInstance().fromJson(json, clazz);

    }

}
