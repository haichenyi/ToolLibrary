package com.haichenyi.aloe.tools;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Title: MemorySpaceUtils
 * @Description: 存储空间工具类,
 * 如果要返回：以M,G为单位的容量的字符串：Formatter.formatFileSize(context, long)
 * @Author: wz
 * @Date: 2018/5/23
 * @Version: V1.0
 */
public class MemorySpaceUtils {
    /**
     * 获取网络文件的大小（需要新开线程）.
     *
     * @param url 文件的url路径
     * @return -1 未查询到或者超出Integer最大值
     * @throws IOException IOException
     */
    public static int FileSize(String url) throws IOException {
        URL url1 = new URL(url);
        // 创建连接
        final HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        //处理下载读取长度为-1 问题
        conn.setRequestProperty("Accept-Encoding", "identity");
        conn.connect();
        // 获取文件大小
        return conn.getContentLength();
    }

    /**
     * 判断sd卡是否可用.
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部存储空间/data.
     *
     * @return long
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getInternalMemorySize() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        //获取单个数据块的大小
        long blockSizeLong = statFs.getBlockSizeLong();
        //获取数据快的数量
        long blockCountLong = statFs.getBlockCountLong();
        return blockCountLong * blockSizeLong;
    }

    /**
     * 获取手机内部可用存储空间.
     *
     * @return long
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableInternalMemorySize() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return availableBlocksLong * blockSizeLong;
    }

    /**
     * 获取手机外部存储空间/storage/emulated/0.
     *
     * @return long
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getExternalMemorySize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        return blockCountLong * blockSizeLong;
    }

    /**
     * 获取手机外部可用存储空间.
     *
     * @return long
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableExternalMemorySize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return availableBlocksLong * blockSizeLong;
    }

    /**
     * 获取给定目录的可用空间
     *
     * @param filePath 目录
     * @return long
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getPathMemorySize(String filePath) {
        StatFs statFs = new StatFs(filePath);
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return availableBlocksLong * blockSizeLong;
    }

    /**
     * 获取给定文件夹的可用空间
     *
     * @param file 文件夹
     * @return long
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getFileMemorySize(File file) {
        return getPathMemorySize(file.getPath());
    }
}
