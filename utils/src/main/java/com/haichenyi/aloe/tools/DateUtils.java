package com.haichenyi.aloe.tools;

import android.content.Context;
import android.text.format.DateFormat;

/**
 * @Title: DateUtils
 * @Description: 日期工具类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public final class DateUtils {
    private DateUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /**
     * 格式化时间
     * <p>
     * "MM/dd/yy h:mmaa" -> "11/03/87 11:23am"
     * "MMM dd, yyyy h:mmaa" -> "Nov 3, 1987 11:23am"
     * "MMMM dd, yyyy h:mmaa" -> "November 3, 1987 11:23am"
     * "E, MMMM dd, yyyy h:mmaa" -> "Tues , November 3, 1987 11:23am"
     * "EEEE, MMMM dd, yyyy h:mmaa" -> "Tues day, Nov 3, 1987 11:23am"
     * "YYYY年MM月dd日,kk:mm"  -> 2014年09月30日,11:23
     * "yyyy-MM-dd HH:mm:ss" -> 2018-05-22 10:52:33
     *
     * @param format 时间格式
     * @param date   long类型的时间（System.currentTimeMillis()）
     * @return 格式后的字符串
     */
    public static String getDate(final String format, final long date) {
        return (String) DateFormat.format(format, date);
    }

    /**
     * 判断是否是24小时格式
     *
     * @param context context
     * @return boolean
     */
    public static boolean is24HourFormat(final Context context) {
        return DateFormat.is24HourFormat(context);
    }
}
