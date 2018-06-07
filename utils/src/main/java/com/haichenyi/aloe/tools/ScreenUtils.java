package com.haichenyi.aloe.tools;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;


/**
 * @Title: ScreenUtils
 * @Description: 屏幕工具类
 * @Author: wz
 * @Date: 2018/5/23
 * @Version: V1.0
 */
public final class ScreenUtils {

    private ScreenUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /**
     * 获取屏幕亮度.
     *
     * @param context context
     * @return int 0-255之间
     */
    public static int getSystemBrightness(final Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 改变App当前Window亮度.
     *
     * @param activity   activity
     * @param brightness 亮度 0-255之间
     */
    public static void changeAppBrightness(final Activity activity, final int brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    /**
     * 设置当前系统屏幕亮度值.
     *
     * @param context  context
     * @param paramInt 0--255
     */
    public static void saveScreenBrightness(final Context context, final int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 设置当前屏幕亮度的模式.
     *
     * @param context context
     * @param type    类型 自动：{@link Settings.System#SCREEN_BRIGHTNESS_MODE_AUTOMATIC}
     *                手动：{@link Settings.System#SCREEN_BRIGHTNESS_MODE_MANUAL}
     */
    public static void setScreenMode(final Context context, final int type) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, type);
            //保存到系统中
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
            context.getContentResolver().notifyChange(uri, null);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 获取当前屏幕的亮度模式.
     *
     * @param context context
     * @return int 自动：{@link Settings.System#SCREEN_BRIGHTNESS_MODE_AUTOMATIC} = 1
     * 手动：{@link Settings.System#SCREEN_BRIGHTNESS_MODE_MANUAL} = 0
     */
    public static int getScreenMode(final Context context) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return screenMode;
    }

    /**
     * 修改系统的亮度值.
     *
     * @param context    context
     * @param brightness 亮度值
     */
    public static void saveBrightness(final Context context, final int brightness) {
        //改变系统的亮度值
        //这里需要权限android.permission.WRITE_SETTINGS
        //设置为手动调节模式
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //保存到系统中
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);
        context.getContentResolver().notifyChange(uri, null);
    }

    /**
     * 获取系统当前的休眠时间.
     *
     * @param context context
     * @return 休眠时间，毫秒
     */
    public static int getScreenOffTime(final Context context) {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screenOffTime;
    }

    /**
     * 设置系统当前的休眠时间.
     *
     * @param context context
     * @param time    时间（单位：分钟）
     */
    public static void setScreenOffTime(final Context context, final int time) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, time);
            //保存到系统中
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
            context.getContentResolver().notifyChange(uri, null);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}
