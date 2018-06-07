package com.haichenyi.aloe.interfaces;

/**
 * @Title: DownloadListener
 * @Description: 下载监听
 * @Author: wz
 * @Date: 2018/5/24
 * @Version: V1.0
 */
public interface DownloadListener {
    /**
     * 下载失败
     *
     * @param e 异常
     */
    void onFailed(Exception e);

    /**
     * 正在下载
     *
     * @param current  当前大小
     * @param total    总大小
     * @param progress 进度
     */
    void onProgress(long current, long total, int progress);

    /**
     * 下载成功
     *
     * @param filePath 下载文件的路径
     */
    void onSuccess(String filePath);
}
