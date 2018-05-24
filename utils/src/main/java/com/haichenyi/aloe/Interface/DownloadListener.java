package com.haichenyi.aloe.Interface;

/**
 * @Title: DownloadListener
 * @Description: 下载监听
 * @Author: wz
 * @Date: 2018/5/24
 * @Version: V1.0
 */
public interface DownloadListener {
    void onFailed(Exception e);

    void onProgress(long current, long total, int progress);

    void onSuccess(String filePath);
}
