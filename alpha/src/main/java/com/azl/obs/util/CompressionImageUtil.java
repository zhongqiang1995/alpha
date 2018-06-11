package com.azl.obs.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.azl.util.ScreenUtil;
import com.azl.util.StreamHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Created by zhong on 2018/3/23.
 */

public class CompressionImageUtil {


    public static String getCompressionPath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    public static boolean isImageContentType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null || !contentType.startsWith("image")) {
            return false;
        }
        return true;
    }

    /**
     * @param targetFile
     * @param context
     * @param maxSize
     * @return
     */
    public static File getCompressionImgFile(File targetFile, Context context, int maxSize, String newFileName) {
        if (targetFile == null || !targetFile.exists()) {
            return null;
        }

        if (!isImageContentType(targetFile.getAbsolutePath())) {
            return null;
        }
        int height = ScreenUtil.getScreenHeight(context);
        int width = ScreenUtil.getScreenWidth(context);

        File newFile = new File(getCompressionPath(context), newFileName);
        if (newFile.exists()) {
            newFile.delete();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(targetFile.getPath(), options);

        options.inJustDecodeBounds = false;

        int bitmapW = options.outWidth;
        int bitmapH = options.outHeight;

        int f = 1;
        if (bitmapW > bitmapH && bitmapW > width) {
            f = bitmapW / width;
        } else if (bitmapW < bitmapH && bitmapH > height) {
            f = bitmapH / height;
        }


        if (f < 0) {
            f = 1;
        }
        options.inSampleSize = f;//压缩比例

        bitmap = BitmapFactory.decodeFile(targetFile.getPath(), options);
        if (f > 1) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(newFile));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        int count;
        if (newFile.exists()) {
            //进行过尺寸压缩
            count = (int) (newFile.length() / 1024);
        } else {
            //没有进行尺寸压缩
            count = (int) (targetFile.length() / 1024);
        }
        boolean isCompress = false;

        if (count > maxSize) {
            compressBmpToFile(bitmap, newFile, maxSize);
            isCompress = true;
        } else if (f > 1) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(newFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (isCompress || f > 1) {
            return newFile;
        }
        return null;
    }

    /**
     * 压缩图片质量
     *
     * @param bmp     目标bitmap
     * @param outFile 输出文件
     * @param maxSize Kb为单位
     */
    public static void compressBmpToFile(Bitmap bmp, File outFile, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = null;
        int options = 80;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length!=0&&baos.toByteArray().length / 1024 > maxSize) {
            baos.reset();
            options -= 10;

            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            if (options <= 10) {
                break;
            }
        }

        try {
            if (outFile.exists()) {
                outFile.delete();
            }
            fos = new FileOutputStream(outFile);
            fos.write(baos.toByteArray());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamHelper.close(baos, fos);
        }
    }
}
