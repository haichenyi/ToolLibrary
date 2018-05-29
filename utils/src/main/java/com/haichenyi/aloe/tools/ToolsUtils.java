package com.haichenyi.aloe.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.File;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Title: ToolsUtils
 * @Description: 工具类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public class ToolsUtils {
    /**
     * 通过资源名称获取资源ID.
     *
     * @param context      上下文
     * @param defType      资源文件目录"drawable"等
     * @param resourceName 资源名称
     * @return 资源ID
     */
    public static int getResourceId(Context context, String defType, String resourceName) {
        return context.getResources().getIdentifier(resourceName, defType, context.getPackageName());
    }

    /**
     * 判断是否满足某个正则表达式
     *
     * @param number  需要判断的字符串
     * @param regular 正则表达式：判断是否是手机号："^(1(3|5|8)\\d{9})|(14(5|7)\\d{8})|(17([^2|4|9)])\\d{8})$"
     * @return true是满足, false不满足
     */
    public static boolean isRegular(String number, String regular) {
        Pattern p = Pattern.compile(regular);
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * 返回你需要的值：这里你传20，单位你传dp，返回值就是你这个手机对应的20dp
     *
     * @param value 数据
     * @param unit  单位：dp为{@link TypedValue#COMPLEX_UNIT_DIP},
     *              sp为{@link TypedValue#COMPLEX_UNIT_SP},
     *              px为{@link TypedValue#COMPLEX_UNIT_PX},
     *              其它请看{@link TypedValue#applyDimension(int, float, DisplayMetrics)}
     * @return 具体长度数值
     */
    public static float digitValue(float value, int unit) {
        return TypedValue.applyDimension(unit, value, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 打开设置界面.
     */
    public static void startSetting(Activity activity) {
        PermissionUtils.setPermissionsSetting(activity);
    }

    /**
     * 截取浮点数
     *
     * @param aFloat 需要截取的数
     * @param num    小数点后面保留几位
     * @return 截取后的数
     */
    public static Float getFloat(Float aFloat, int num) {

        return new BigDecimal(aFloat).setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * APP下载成功之后跳转安装界面
     *
     * @param context  context
     * @param filePath app存储路径
     */
    public static void startInstall(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
