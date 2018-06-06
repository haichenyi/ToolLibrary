package com.haichenyi.aloe.tools;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.haichenyi.aloe.Interface.DownloadListener;
import com.haichenyi.aloe.Interface.HttpCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private Headers getHeaders(final ArrayMap<String, Object> params) {
        return new Headers.Builder().add(new Gson().toJson(params)).build();
    }

    /**
     * 获取RequestBody对象
     *
     * @param bodyParams Map<String, Object>
     * @return RequestBody
     */
    private RequestBody getRequestBody(final ArrayMap<String, Object> bodyParams) {
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

    public void post(final String url, final ArrayMap<String, Object> headParams,
                     final ArrayMap<String, Object> bodyParams, final HttpCallback callback) {
        try {
            Headers headers = this.getHeaders(headParams);
            RequestBody body = this.getRequestBody(bodyParams);
            LogUtils.i(TAG, "url：" + url + "\nheader:" + new Gson().toJson(headParams)
                    + "\nbody:" + new Gson().toJson(bodyParams));
            Request request = (new Request.Builder()).url(url).post(body).headers(headers).build();
            onPostStart(callback);
            this.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String result = "";
                        ResponseBody body1 = response.body();
                        if (body1 != null) {
                            result = body1.toString();
                        }
                        LogUtils.i(TAG, "success：" + result);
                        onPostSuccess(callback, result);
                    } else {
                        onPostError(callback, response.message());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    onPostError(callback, e.getMessage());
                }
            });
        } catch (Exception var8) {
            LogUtils.e(TAG, var8.getMessage());
            var8.printStackTrace();
        }
    }

    private void onPostStart(final HttpCallback callback) {
        if (null != callback) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStart();
                }
            });
        }
    }

    private void onPostSuccess(final HttpCallback callback, final String data) {
        if (null != callback) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(data);
                }
            });
        }
    }

    private void onPostError(final HttpCallback callback, final String msg) {
        if (null != callback) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(msg);
                }
            });
        }
    }

    /**
     * 下载文件
     *
     * @param url      下载路径
     * @param dirPath  文件夹路径(文件夹不存在会自动创建)
     * @param fileName 文件名称(传""就从url最后面截取)
     * @param listener 下载监听(回调在主线程)
     */
    public void downLoad(final String url, final String dirPath, final String fileName,
                         final DownloadListener listener) {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        Request request = new Request.Builder().url(url).build();
        this.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 下载失败
                onDownLoadFailed(listener, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                long current = 0;
                long total;

                try {
                    ResponseBody body = response.body();
                    if (null != body) {
                        is = body.byteStream();
                        total = body.contentLength();
                    } else {
                        return;
                    }
                    // 储存下载文件的目录
                    FileUtils.createFileDirs(dirPath);
                    File file;
                    if (StringUtils.isEmpty(fileName)) {
                        file = FileUtils.createNewFile(dirPath, StringUtils.getNameFromUrl(url));
                    } else {
                        file = FileUtils.createNewFile(dirPath, fileName);
                    }
                    //断言为真，如果断言为false，则会抛出一个AssertionError对象。这个AssertionError继承于Error对象
                    assert file != null;
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        int progress = (int) (current * 1.0f / total * 100);
                        // 下载中
                        onDownLoadProgress(listener, current, total, progress);
                    }
                    fos.flush();
                    // 下载完成
                    onDownLoadSuccess(listener, file.getAbsolutePath());
                } catch (Exception e) {
                    onDownLoadFailed(listener, e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        LogUtils.i(LogUtils.TAG_Wz, e.getMessage());
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        LogUtils.i(LogUtils.TAG_Wz, e.getMessage());
                    }
                }
            }
        });
    }

    private void onDownLoadFailed(final DownloadListener listener, final Exception e) {
        if (null != listener) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFailed(e);
                }
            });
        }
    }

    private void onDownLoadSuccess(final DownloadListener listener, final String filePath) {
        if (null != listener) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(filePath);
                }
            });
        }
    }

    private void onDownLoadProgress(final DownloadListener listener, final long current,
                                    final long total, final int progress) {
        if (null != listener) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onProgress(current, total, progress);
                }
            });
        }
    }
}
