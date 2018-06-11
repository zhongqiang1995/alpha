package com.azl.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by zhong on 2017/11/16.
 */

public class MD5Util {
    /**
     * 计算文件的md5
     *
     * @param filePath
     * @return FileMD5
     */
    public static String encodeFileByMd5(String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile()) {
            return "";
        }
        try {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte buffer[] = new byte[1024];
            int len;
            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
