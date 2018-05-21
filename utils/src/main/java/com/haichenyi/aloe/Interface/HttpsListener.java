package com.haichenyi.aloe.Interface;

/**
 * @Title: HttpsListener
 * @Description: https网络请求的监听
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public interface HttpsListener {
    void onSuccess(String success);
    void onFailed(String failed);
}
