package com.azl.view.grid.image.util;

import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by zhong on 2018/3/5.
 */

public class FrescoTypeJumpUtil {

    public static final String PREFIX_LOCAL = "file://";
    public static final String PREFIX_SRC = "res:// /";
    public static final String PREFIX_ASSETS = "asset:///";

    public enum DataType {
        SRC,//资源id
        ASSETS, //app中assets文件夹下
        URL, //远程路径
        LOCAL,//sd中的路径
        UN_KNOWN//未知
    }

    public static DataType encodingType(Object obj) {
        if (obj == null) {
            return DataType.UN_KNOWN;
        }
        if (obj instanceof String) {
            String str = (String) obj;
            if (TextUtils.isEmpty(str)) {
                return DataType.UN_KNOWN;
            }
            if (str.startsWith("file:///android_asset")) {
                //在assets文件中
                return DataType.ASSETS;
            } else if (str.startsWith("/")) {
                return DataType.LOCAL;
            } else if (str.startsWith("http://") || str.startsWith("https://")) {
                return DataType.URL;
            }
        } else if (obj instanceof Integer) {
            return DataType.SRC;
        }
        return DataType.UN_KNOWN;
    }

    public static DataType decodingType(Uri uri) {
        String path = uri.toString();

        if (TextUtils.isEmpty(path)) {
            return DataType.UN_KNOWN;
        }
        if (path.startsWith(PREFIX_ASSETS)) {
            return DataType.ASSETS;
        } else if (path.startsWith(PREFIX_LOCAL)) {
            return DataType.LOCAL;
        } else if (path.startsWith("http://") || path.startsWith("https://")) {
            return DataType.URL;
        } else if (path.startsWith(PREFIX_SRC)) {
            return DataType.SRC;
        }

        return DataType.UN_KNOWN;
    }

    public static String decodingPath(Uri uri) {
        String path = uri.toString();
        DataType type = decodingType(uri);
        String newPath = "";

        if (type == DataType.URL) {
            return path;
        } else if (type == DataType.ASSETS) {
            String s = PREFIX_ASSETS;
            if (path.startsWith(s)) {
                newPath = "file:///android_asset/" + path.substring(s.length(), path.length());
            }
        } else if (type == DataType.LOCAL) {
            String s = PREFIX_LOCAL;
            if (path.startsWith(s)) {
                newPath = path.substring(s.length(), path.length());
            }
        } else if (type == DataType.SRC) {
            String s = PREFIX_SRC;
            if (path.startsWith(s)) {
                newPath = path.substring(s.length(), path.length());
            }
        } else {
            newPath = path;
        }

        return newPath;
    }

    public static String formatAssetsPath(String assetsPath) {
        if (TextUtils.isEmpty(assetsPath)) {
            return "";
        }
        String s = "file:///android_asset/";
        if (assetsPath.startsWith(s)) {
            String newPath = assetsPath.substring(s.length(), assetsPath.length());
            return PREFIX_ASSETS + newPath;
        }
        return "";
    }

    public static Uri getUri(Object obj) {
        FrescoTypeJumpUtil.DataType type = FrescoTypeJumpUtil.encodingType(obj);
        Uri uri;
        if (type == FrescoTypeJumpUtil.DataType.ASSETS) {
            uri = Uri.parse(FrescoTypeJumpUtil.formatAssetsPath(obj + ""));
        } else if (type == FrescoTypeJumpUtil.DataType.LOCAL) {
            uri = Uri.parse(PREFIX_LOCAL + obj);
        } else if (type == FrescoTypeJumpUtil.DataType.URL) {
            uri = Uri.parse(obj + "");
        } else if (type == FrescoTypeJumpUtil.DataType.SRC) {
            int id = (int) obj;
            uri = Uri.parse(PREFIX_SRC + id);
        } else {
            uri = Uri.parse("");

        }
        return uri;
    }


}
