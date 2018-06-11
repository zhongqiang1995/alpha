package com.azl.util;


import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhong on 2018/4/2.
 */

public class FileUtil {

    /**
     * @param targetFile 目标路径
     * @param sourceFile 拷贝文件的路径
     * @return
     */
    public static boolean copy(File targetFile, File sourceFile) {
        if (sourceFile == null || !sourceFile.exists() || targetFile == null) {
            return false;
        }
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;

        boolean isSuccess = false;
        try {
            if (targetFile.exists()) {
                targetFile.delete();
            }
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(targetFile);

            isSuccess = copy(inputStream, outputStream, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamHelper.close(outputStream, inputStream);
        }


        return isSuccess;
    }

    /**
     * @param inputStream  输入文件流
     * @param outputStream 输出文件流
     * @param isClose      是否要关闭流
     * @return 是否copy成功
     */
    public static boolean copy(InputStream inputStream, OutputStream outputStream, boolean isClose) {
        boolean isCopySuccess = false;
        try {
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            outputStream.flush();
            isCopySuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isClose) {
                StreamHelper.close(outputStream, inputStream);
            }
        }

        return isCopySuccess;
    }

    /**
     * 获取后缀名字
     *
     * @param path 路径名字
     * @return 后缀名，没有返回为空
     */
    public static String getSuffixName(String path) {
        String fileName = "";
        if (path == null) {
            return "";
        }
        int start = -1;
        if (path.startsWith("http://") || path.startsWith("https://")) {
            int i = path.lastIndexOf("/");
            start = i + 1;

        } else {
            int i = path.lastIndexOf(File.separator);
            start = i + 1;
        }
        if (start >= 0 && start <= path.length()) {
            fileName = path.substring(start);
        }
        int index;
        if (TextUtils.isEmpty(fileName) || (index = fileName.lastIndexOf(".")) == -1) {
            return "";
        }

        start = index + 1;
        if (start > fileName.length()) {
            return "";
        }

        String suffix = fileName.substring(start);
        return suffix;
    }

}
