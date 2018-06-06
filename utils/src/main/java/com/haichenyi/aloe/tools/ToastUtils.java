package com.haichenyi.aloe.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.haichenyi.aloe.BuildConfig;

/**
 * @Title: ToastUtils
 * @Description: 实时更新的Toast，用之前需要初始化一遍，最好就在Application调用init()方法.
 * @Author: 海晨忆
 * @Date: 2018/5/17
 * @Version: V1.0
 */

public final class ToastUtils {
    private static Toast toast;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private ToastUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    @SuppressWarnings("all")
    public static void init(final Context context) {
        if (toast == null) {
            synchronized (ToastUtils.class) {
                if (toast == null) {
                    mContext = context;
                    toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    public static void showTipMsg(final String msg) {
        if (null == toast) {
            init(mContext);
        }
        toast.setText(msg);
        toast.show();
    }

    public static void showTipMsg(@StringRes final int msg) {
        if (null == toast) {
            init(mContext);
        }
        toast.setText(msg);
        toast.show();
    }

    /**
     * 调试模式下显示Toast.
     *
     * @param txt Toast显示内容
     */
    public static void debugShow(@StringRes final int txt) {
        if (null == toast) {
            init(mContext);
        }
        if (!BuildConfig.DEBUG) {
            return;
        }
        toast.setText(txt);
        toast.show();
    }

    public static void debugShow(final String txt) {
        if (null == toast) {
            init(mContext);
        }
        if (!BuildConfig.DEBUG) {
            return;
        }
        toast.setText(txt);
        toast.show();
    }
}
