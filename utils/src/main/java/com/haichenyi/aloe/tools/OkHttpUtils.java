package com.haichenyi.aloe.tools;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.haichenyi.aloe.Interface.HttpCallback;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @Title: OkHttpUtils
 * @Description: okHttp的网络请求类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public class OkHttpUtils {
    private static String TAG = "HttpUtils";
    private OkHttpClient client = new OkHttpClient();
    public static final int TIMEOUT = 20000;
    private static final String paramsEncoding = "UTF-8";
    private static final OkHttpUtils mInstance = new OkHttpUtils();
    private Handler handler = new Handler(Looper.getMainLooper());

    public static OkHttpUtils getInstance() {
        return mInstance;
    }

    private OkHttpUtils() {
        this.client.newBuilder().connectTimeout(20000L, TimeUnit.SECONDS)
                .writeTimeout(20000L, TimeUnit.SECONDS)
                .readTimeout(20000L, TimeUnit.SECONDS).build();
    }

    /**
     * 获取header对象
     *
     * @param params Map<String, Object>
     * @return Headers
     */
    private Headers getHeaders(ArrayMap<String, Object> params) {
        return new Headers.Builder().add(new Gson().toJson(params)).build();
    }

    /**
     * 获取RequestBody对象
     *
     * @param bodyParams Map<String, Object>
     * @return RequestBody
     */
    private RequestBody getRequestBody(ArrayMap<String, Object> bodyParams) {
        okhttp3.FormBody.Builder formEncodingBuilder = new okhttp3.FormBody.Builder();
        if (null != bodyParams) {
            for (Object o : bodyParams.entrySet()) {
                Map.Entry<String, Object> entry = (Map.Entry) o;
                formEncodingBuilder.add(entry.getKey() == null ? "" : entry.getKey(), entry.getValue() == null ?
                        "" : entry.getValue().toString());
            }
        }
        return formEncodingBuilder.build();
    }

    public void post(String url, ArrayMap<String, Object> headParams, ArrayMap<String, Object> bodyParams, final HttpCallback callback) {
        try {
            Headers headers = this.getHeaders(headParams);
            RequestBody body = this.getRequestBody(bodyParams);
            LogUtils.i(TAG, "url：" + url + "\nheader:" + new Gson().toJson(headParams)
                    + "\nbody:" + new Gson().toJson(bodyParams));
            Request request = (new Request.Builder()).url(url).post(body).headers(headers).build();
            this.onStart(callback);
            this.client.newCall(request).enqueue(new Callback() {
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String result = "";
                        ResponseBody body1 = response.body();
                        if (body1 != null) {
                            result = body1.toString();
                        }
                        LogUtils.i(TAG, "success：" + result);
                        onSuccess(callback, result);
                    } else {
                        onError(callback, response.message());
                    }
                }

                public void onFailure(Call call, IOException e) {
                    onError(callback, e.getMessage());
                }
            });
        } catch (Exception var8) {
            LogUtils.e(TAG, var8.getMessage());
            var8.printStackTrace();
        }
    }

    private void onStart(final HttpCallback callback) {
        if (null != callback) {
            this.handler.post(new Runnable() {
                public void run() {
                    callback.onStart();
                }
            });
        }

    }

    private void onSuccess(final HttpCallback callback, final String data) {
        if (null != callback) {
            this.handler.post(new Runnable() {
                public void run() {
                    callback.onSuccess(data);
                }
            });
        }

    }

    private void onError(final HttpCallback callback, final String msg) {
        if (null != callback) {
            this.handler.post(new Runnable() {
                public void run() {
                    callback.onError(msg);
                }
            });
        }

    }
}
