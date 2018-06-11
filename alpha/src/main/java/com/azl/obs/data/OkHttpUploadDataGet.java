package com.azl.obs.data;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.azl.file.bean.Info;
import com.azl.file.download.request.RequestUploadBody;
import com.azl.file.helper.D;
import com.azl.obs.util.CompressionImageUtil;
import com.azl.util.MD5Util;
import com.azl.util.OkHttpHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhong on 2017/11/16.
 */

public class OkHttpUploadDataGet extends OkHttpFileDataGet {

    public OkHttpUploadDataGet(String path, java.lang.reflect.Type type, Type method, Map<String, String> map, String localPath, String mark, Object tab) {
        super(path, type, method, map, localPath, mark, tab);
    }

    @Override
    protected void doAction(String url) {
        Info info = getInfo();
        OkHttpClient client = OkHttpHelper.getClient();
        File compressionFile = null;
        try {
            if (!judgePermission()) {
                //无权限读取文件
                info.setStatus(Info.STATUS_ERROR);
                info.setInfo("权限不足");
                return;
            }

            File file = new File(getLocalPath());
            if (!file.exists() || !file.isFile()) {
                info.setStatus(Info.STATUS_ERROR);
                info.setInfo("文件不存在");
                return;
            }
            info.setStatus(Info.STATUS_READY);
            info.setInfo("准备中");
            handleInfo();

            //计算文件md5
            String md5 = MD5Util.encodeFileByMd5(file.getAbsolutePath());


            //图片压缩后在上传
            if (CompressionImageUtil.isImageContentType(file.getAbsolutePath())) {
                String compressionPath = CompressionImageUtil.getCompressionPath(D.APP) + File.separator + md5 + ".jpg";
                compressionFile = new File(compressionPath);
                if (!compressionFile.exists()) {
                    compressionFile = CompressionImageUtil.getCompressionImgFile(file, D.APP, 100, md5 + ".jpg");
                }
            }


            File uploadFile;
            if (compressionFile != null && compressionFile.exists()) {
                uploadFile = compressionFile;
            } else {
                uploadFile = file;
            }


            if (info.isComplete()) {
                String currentMd5 = info.getMark();
                if (currentMd5 != null && !currentMd5.equals("") && currentMd5 != null && currentMd5.equals(md5)) {
                    info.setStatus(Info.STATUS_COMPLETE);
                    info.setInfo("完成");
                    return;
                }
            }


            info.setProgress(0);
            info.setMark(md5);
            info.setLength(uploadFile.length());
            info.setPath(url);
            info.setLocalPath(file.getAbsolutePath());
            handleInfo();


            RequestBody requestBody = getFileBody(uploadFile, file.getName());

            Request.Builder builder = new Request.Builder();

            Request request = builder.post(requestBody).url(url).build();

            Call call = client.newCall(request);
            setCall(call);
            info.setStatus(Info.STATUS_PROGRESS);
            info.setInfo("上传中");
            Response response = call.execute();
            boolean is = response.isSuccessful();

            if (is) {
                //上传成功
                String body = response.body().string();
                if (body == null) {
                    throw new RuntimeException("response body is null");
                } else {
                    JSONObject obj = new JSONObject(body);
                    int code = obj.getInt("code");
                    if (code != 0) {
                        throw new RuntimeException(body + "");
                    }
                }

                info.setData(body);
                info.setStatus(Info.STATUS_COMPLETE);
                info.setInfo("完成");
                info.setCompleteTime(System.currentTimeMillis());
                Log.e(TAG, body);
            } else {
                //上传失败
                if (info.getStatus() != Info.STATUS_PAUSE) {
                    info.setStatus(Info.STATUS_ERROR);
                    info.setInfo("上传失败");
                }

            }


        } catch (SocketException e) {
        } catch (Exception e) {
            if (info.getStatus() != Info.STATUS_PAUSE) {
                info.setStatus(Info.STATUS_ERROR);
                info.setInfo("上传失败");
                e.printStackTrace();

            }
        } finally {
            if (compressionFile != null&&compressionFile.exists()) {
                compressionFile.delete();
            }
        }


    }

    private boolean judgePermission() {
        int permission = ActivityCompat.checkSelfPermission(D.APP, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean isPermission = permission == PackageManager.PERMISSION_GRANTED;
        return isPermission;
    }

    @Override
    public void cancel() {
        super.cancel();

    }

    private RequestBody getFileBody(File file, String fileName) {
        long length = file.length();
        RequestBody fileBody = RequestUploadBody.create(MediaType.parse("application/octet-stream"), file, new UploadListener());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"alpha\""),
                        RequestBody.create(null, "HGR"))
                .addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"file\"; filename=\"" + fileName + "\""), fileBody)
                .build();
        return requestBody;
    }

    class UploadListener implements RequestUploadBody.UploadProgressListener {


        public static final long skipTime = 1000;
        long sum;
        long time;

        long oldProgress;

        @Override
        public void progress(long total, long current) {
            Info info = getInfo();
            if (info == null) return;


            if (System.currentTimeMillis() - time >= skipTime) {//获取每秒速度
                long speed = current - oldProgress;
                info.setSpeed(speed);
                oldProgress = current;
            }
            info.setProgress(current);
            if (info.isProgress()) {
                handleInfo();
            }
        }
    }


}
