package com.haichenyi.aloe.interfaces;

/**
 * @Title: HttpCallback
 * @Description: okHttp的请求回调
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public interface HttpCallback {
    /**
     * 开始请求
     */
    void onStart();

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
    void onError(String failed);
}
