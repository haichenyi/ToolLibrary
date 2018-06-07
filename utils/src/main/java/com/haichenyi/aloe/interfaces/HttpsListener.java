package com.haichenyi.aloe.interfaces;

/**
 * @Title: HttpsListener
 * @Description: https网络请求的监听
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public interface HttpsListener {
    /**
     * 请求成功
     *
     * @param success success
     */
    void onSuccess(String success);

    /**
     * 请求失败
     *
     * @param failed failed
     */
    void onFailed(String failed);
}
