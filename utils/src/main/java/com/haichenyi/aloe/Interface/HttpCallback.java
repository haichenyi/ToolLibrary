package com.haichenyi.aloe.Interface;

/**
 * @Title: HttpCallback
 * @Description: okHttp的请求回调
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public interface HttpCallback {
    void onStart();

    void onSuccess(String success);

    void onError(String failed);
}
