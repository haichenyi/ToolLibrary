package com.haichenyi.aloe.tools;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title: DateUtils
 * @Description: 日期工具类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public class DateUtils {
    /**
     * 格式化时间
     *
     * @param pattern 时间格式（"yyyy-MM-dd HH:mm:ss"）
     * @param date    long类型的时间（System.currentTimeMillis()）
     * @return 格式后的字符串
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String pattern, long date) {
        return new SimpleDateFormat(pattern).format(new Date(date));
    }
}
