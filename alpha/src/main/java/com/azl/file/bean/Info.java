package com.azl.file.bean;

import android.content.Context;
import android.text.TextUtils;

import com.azl.file.db.ImplFileInfoDB;
import com.azl.file.download.helper.DBHelper;
import com.azl.file.download.impl.ImplDownLoad;

import java.io.File;

/**
 * Created by zhong on 2017/6/14.
 */

public class Info implements Cloneable {

    public static final int STATUS_PROGRESS = 1001;//进行中
    public static final int STATUS_PAUSE = 1002;//暂停
    public static final int STATUS_ERROR = 1003;//出现错误
    public static final int STATUS_COMPLETE = 1004;//完成
    public static final int STATUS_READY = 1005;//准备中
    public static final int STATUS_QUEUE = 1006;//队列中

    private int id;
    private long completeTime;//完成时间
    private long createTime;//创建时间
    private long length;//文件大小
    private long progress;//当前进度
    private int stopCount;//停止过几次
    private String contentType;//内容类型
    private String path;//文件路径
    private String localPath;//完成后的路径
    private int status;
    private String mark;//在下载时候随机生成的字符串，在下载完成前为文件名字
    private String data;
    private long speed;//输入输出的速度
    private String info;//错误信息
    private boolean isRun;//记录当前信息的文件是否正在下载
    private Object tab;
    private int flag;//0是下载 1是上传


    public static final int FLAG_DOWNLOAD = 0;
    public static final int FLAG_UPLOAD = 1;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setTab(Object tab) {
        this.tab = tab;
    }

    public Object getTab() {
        return tab;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public boolean isRun() {
        return isRun;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }


    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getSpeed() {
        return speed;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public int getStopCount() {
        return stopCount;
    }

    public void setStopCount(int stopCount) {
        this.stopCount = stopCount;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private File completeFile;

    public File getCompleteFile() {

        if (!TextUtils.isEmpty(getLocalPath())) {
            completeFile = new File(getLocalPath());
        }

        return completeFile;
    }

    public boolean isComplete() {

        if (status == STATUS_COMPLETE) {
            if (getFlag() == FLAG_UPLOAD) {
                long currentTime = System.currentTimeMillis();
                long completeTime = getCompleteTime();
                long c = currentTime - completeTime;
                c = Math.abs(c);
                if (c > 86400000 * 3) {//大于7天不使用缓存数据重新上传
                    return false;
                }
                return true;
            }
            if (getProgress() == getLength()) {
                return judgeCompleteFileLegal();
            }
        }
        return false;
    }

    public boolean judgeCompleteFileLegal() {
        if (!TextUtils.isEmpty(getLocalPath())) {
            File file = getCompleteFile();
            if (file != null && file.exists() && file.length() == getLength()) {
                return true;
            }
        }
        return false;
    }

    public boolean isProgress() {
        return status == STATUS_PROGRESS;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void update(Context context) {
        try {
            ImplFileInfoDB db = DBHelper.getDB(context, ImplDownLoad.TABLE_NAME);
            db.updateMemoryCache(flag == FLAG_DOWNLOAD ? getPath() : getLocalPath(), this);
            db.update(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCompleteName(Info info) {
        if (localPath == null || path == null) return;
        File file = new File(localPath);
        if (file.exists()) {
            //获取路径结尾的文件名字
            String[] arr = path.split("\\?");
            String head = "";
            if (arr.length != 0) {
                head = arr[0];//获取url部分，忽略参数部分
            }
            int index = head.lastIndexOf("/");
            if (index == -1) {
                return;
            }
            String fileName = head.substring(index, head.length());
            File newFile = new File(file.getParent(), fileName);
            file.renameTo(newFile);
            info.setLocalPath(newFile.getAbsolutePath());

        }
    }

    /**
     * 重新下载 还原变量
     */
    public void resetDownloadVar() {
        progress = 0;
        stopCount = 0;
    }

    @Override
    protected Object clone() {
        Info info = null;

        try {
            info = (Info) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return info;
    }

    public Info copy() {
        return (Info) clone();
    }

    /**
     * @return 返回格式化后的每秒速度
     */
    public String formatSpeedText() {
        long speed = getSpeed();
        if (speed < 1024) {
            return speed + "B/S";
        }
        if (speed == 0 || ((speed / 1024) < 1024)) {
            long kb = speed == 0 ? 0 : (speed / 1024);
            return kb + "KB/S";
        }
        float f = speed / (1024 * 1024 * 1.0f);
        String sf = String.format("%.2f", f);
        return sf + "MB/S";
    }


    /**
     * @return 返回预计下载时间
     */
    public String formatRemainingTime() {
        String returnText = "";
        long s = getLength() - getProgress();
        if (s == 0 || getSpeed() == 0) {
            if (isComplete()) {
                returnText = "完成";
            } else {
                returnText = "未知";
            }
            return returnText;
        }

        long ss = s / getSpeed();//秒

        if (ss < 60) {
            returnText = ss + "秒";

            return returnText;
        }
        long m = ss / 60;//分
        long sm = ss % 60;//取余后的秒
        if (m < 60) {
            returnText = m + "分";
            if (sm != 0) {
                returnText = returnText + sm + "秒";
            }
            return returnText;
        }

        long h = m / 60;//小时
        long sh = m & 60;//取余后的分
        returnText = h + "小时";
        if (sh != 0) {
            returnText += sh + "分";
        }
        return returnText;
    }

}
