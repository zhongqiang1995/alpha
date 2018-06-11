package com.azl.obs.data;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.azl.handle.action.HandleMsg;
import com.azl.util.GsonUtil;
import com.azl.util.ObjectValueUtil;
import com.azl.util.OkHttpHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhong on 2017/5/15.
 */

public class OkHttpDataGet<T> extends HttpDataGet<T> {

    private static final String TAG = "OkHttpDataGet";
    private OkHttpClient client;
    private java.lang.reflect.Type mType;
    private List<Call> mCallList = new ArrayList<>();
    private List<String> mBodyJsons;

    public OkHttpDataGet(String url, java.lang.reflect.Type type, Type method, Map<String, String> par, List<String> bodyJsons) {
        super(url, type, method, par);
        this.mBodyJsons = bodyJsons;
        this.mType = type;
        init();
    }

    private void init() {
        this.client = OkHttpHelper.getClient();
    }

    public void cancel() {
        for (Call call : mCallList) {
            try {
                call.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute(Map<String, String> parMap) {
        notifyBegin();
        if (isPost()) {
            doPost(parMap);
        } else {
            doGet(parMap);
        }
    }

    @Override
    public void execute() {
        execute(getParams());
    }

    private void doGet(Map<String, String> map) {
        String url = joinPar(getUrl(), map);
        Log.i(TAG, "request value:" + url);
        Request.Builder build = new Request.Builder().url(url).cacheControl(new CacheControl.Builder().maxAge(3600, TimeUnit.SECONDS).build());
        addJsonBody(build);
        Request request = build.build();
        Call call = client.newCall(request);
        mCallList.add(call);
        call.enqueue(new HCall(call));
    }

    private void doPost(Map<String, String> parMap) {
//        String url = getUrl();
        String url = joinPar(getUrl(), parMap);
        Log.i(TAG, "request value:" + url);
//        Request.Builder build = new Request.Builder().url(url).post(joinForm(parMap));
        Request.Builder build = new Request.Builder().url(url).post(joinForm(parMap));
        addJsonBody(build);
        Request request = build.build();
        Call call = client.newCall(request);
        mCallList.add(call);
        call.enqueue(new HCall(call));
    }

    private void addJsonBody(Request.Builder build) {
        if (build == null) return;
        MediaType jsonType = MediaType.parse("application/json");
        if (mBodyJsons != null && !mBodyJsons.isEmpty()) {
            for (String json : mBodyJsons) {
                RequestBody jsonBody = RequestBody.create(jsonType, json);
                build.put(jsonBody);
            }
        }
    }


    private FormBody joinForm(Map<String, String> map) {
        if (map == null || map.isEmpty()) return new FormBody.Builder().build();
        FormBody.Builder build = new FormBody.Builder();
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            build.add(key, value == null ? "" : value);
        }
        return build.build();
    }


    class HCall implements Callback {

        private Call mCall;

        public HCall(Call call) {
            this.mCall = call;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            remoteTag();
            handleError(e.getMessage(), -1, "");
            handleComplete();
        }

        private void remoteTag() {
            mCallList.remove(mCall);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = "";
            try {
                if (response.isSuccessful() && response.body() != null) {
                    json = response.body().string();
                    if (TextUtils.isEmpty(json)) {
                        throw new RuntimeException("load data error");
                    }
                    boolean isReturnSuccess = true;
                    Object object;
                    if (getFormatClass() == String.class || mType == String.class) {
                        object = json;
                    } else {
                        object = GsonUtil.fromJson(json, getFormatClass() == null ? mType : getFormatClass());
                        isReturnSuccess = judgeReturnDataSuccess(object, json);
                    }
                    if (isReturnSuccess) {
                        handleSuccess(object);
                    }
                } else {
                    throw new RuntimeException("load data error");
                }
            } catch (Exception e) {
                handleError(response.message(), response.code(), json);
                e.printStackTrace();
            } finally {
//                response.close();

            }
            handleComplete();
            remoteTag();
        }

    }

    private boolean judgeReturnDataSuccess(Object object, String json) {
        Object code = ObjectValueUtil.getInstance().getValueObject(object, "code");
        if (code != null) {
            int c = code != null ? (int) code : -1;
            if (c != 0) {
                String msg = "";
                try {
                    JSONObject obj = new JSONObject(json);
                    try {
                        msg = obj.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(msg)) {
                        msg = obj.getString("dmsg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handleError(msg == null ? "" : msg, c, "");
                return false;
            }
        }

        return true;

    }

    private static final int HANDLE_SUCCESS = 1;
    private static final int HANDLE_ERROR = 2;
    private static final int HANDLE_COMPLETE = 3;

    private void handleSuccess(Object object) {
        Message message = new Message();
        message.what = HANDLE_SUCCESS;
        message.obj = object;
        mHandle.sendMessage(message);
    }

    private void handleError(String msg, int code, String body) {
        Message message = new Message();
        if (!TextUtils.isEmpty(body)) {
            JSONObject object = null;
            try {
                object = new JSONObject(body);
            } catch (JSONException e) {
            }
            if (object != null) {
                try {
                    int c = object.getInt("code");
                    code = c;
                } catch (JSONException e) {
                }
                try {
                    String ms = object.getString("msg");
                    if (!TextUtils.isEmpty(ms)) {
                        msg = ms;
                    }
                } catch (JSONException e) {
                }
            }
        }
        if (code == 301) {
            HandleMsg.handleMark("loginTimeout");
        }
        message.what = HANDLE_ERROR;
        message.obj = msg;
        message.arg1 = code;
        mHandle.sendMessage(message);
    }

    private void handleComplete() {
        mHandle.sendEmptyMessage(HANDLE_COMPLETE);
    }

    Handler mHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLE_COMPLETE:
                    notifyComplete();
                    break;
                case HANDLE_ERROR:
                    String n = msg.obj == null ? "" : (String) msg.obj;
                    int code = msg.arg1;
                    notifyError(code, n);
                    break;
                case HANDLE_SUCCESS:
                    Object object = msg.obj;
                    notifyNext(object);
                    break;
            }
        }
    };

}
