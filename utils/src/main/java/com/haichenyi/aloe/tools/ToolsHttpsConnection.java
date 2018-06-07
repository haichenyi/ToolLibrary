package com.haichenyi.aloe.tools;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.haichenyi.aloe.interfaces.HttpsListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @Title: ToolsHttpsConnection
 * @Description: https网络请求工具类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public final class ToolsHttpsConnection {
    private static final String TAG = "ToolsHttpsConnection";

    private ToolsHttpsConnection() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /**
     * post请求，记得新开线程
     *
     * @param activity activity
     * @param path     URL路径
     * @param token    请求头的token
     * @param params   参数
     * @param listener 回调监听HttpsListener
     */
    public static void post(final Activity activity, final String path, final String token,
                            final ArrayMap<String, String> params, final HttpsListener listener) {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(createSSL());
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(50 * 1000);
            //添加请求头
            connection.setRequestProperty("token", token);
            connection.connect();
            //post数据
            String requestData = parseParams(params);
            LogUtils.i(TAG, "请求数据 : " + requestData);
            if (!TextUtils.isEmpty(requestData)) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestData.getBytes("UTF-8"));
                outputStream.close();
            }
            //获取返回结果
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final String responseStr = readStream(connection.getInputStream());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(responseStr);
                    }
                });
                LogUtils.i(TAG, "服务端返回的数据：" + responseStr);
            } else {
                int responseCode = connection.getResponseCode();
                final String message = readStream(connection.getErrorStream());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed(message);
                    }
                });
                LogUtils.i(TAG, "请求失败： code=" + responseCode + ", msg=" + message);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onFailed(e.getMessage());
                }
            });
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 读取流
     *
     * @param inputStream 输入流
     * @return StringBuilder.toString()
     * @throws IOException IOException
     */
    private static String readStream(final InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    /**
     * 将map转为字符串
     *
     * @param params 参数map
     * @return String
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    private static String parseParams(final ArrayMap<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            stringBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), "utf-8"));
            stringBuilder.append("&");
        }
        String str = stringBuilder.toString();
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * https的SSL
     *
     * @return ssf
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyManagementException   KeyManagementException
     */
    private static SSLSocketFactory createSSL()
            throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] tm = new TrustManager[]{myX509TrustManager};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tm, null);
        return sslContext.getSocketFactory();
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            LogUtils.i(TAG, "checkClientTrusted");
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            LogUtils.i(TAG, "checkServerTrusted");
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    };
}
