package com.haichenyi.aloe.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;

import java.util.List;

/**
 * @Title: SpUtils
 * @Des: SharedPreference工具类
 * @Author: haichenyi
 * @Date: 2018/6/11
 * @Email: haichenyi@yeah.net
 */
public final class SpUtils {

    private SpUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /**
     * 获取SharedPreference对象
     *
     * @param context context
     * @param spName  spName
     * @return SharedPreference对象
     */
    private static SharedPreferences getSp(final Context context, final String spName) {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 获取Editor对象
     *
     * @param context context
     * @param spName  spName
     * @return Editor对象
     */
    private static SharedPreferences.Editor getSpEditor(final Context context, final String spName) {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE).edit();
    }

    /**
     * 连续存多个值
     *
     * @param context context
     * @param spName  spName
     * @param data    List<Pair>数据
     */
    public static void setListValue(final Context context, final String spName,
                                    final List<Pair> data) {
        SharedPreferences.Editor spEditor = getSpEditor(context, spName);
        for (int i = 0; i < data.size(); i++) {
            Pair pair = data.get(i);
            if (pair.second != null) {
                if (pair.second instanceof Integer) {
                    spEditor.putInt((String) pair.first, (Integer) pair.second);
                } else if (pair.second instanceof Boolean) {
                    spEditor.putBoolean((String) pair.first, (Boolean) pair.second);
                } else {
                    spEditor.putString((String) pair.first, pair.second.toString());
                }
            }
        }
        spEditor.apply();
    }

    /**
     * 存String值
     *
     * @param context context
     * @param spName  存放的sp的名字
     * @param key     存的key
     * @param value   存的值
     */
    public static void setValue(final Context context, final String spName, final String key,
                                final String value) {
        getSp(context, spName).edit().putString(key, value).apply();
    }

    /**
     * 获取String的值
     *
     * @param context context
     * @param spName  获取的sp的名字
     * @param key     key
     * @return value
     */
    public static String getStringValue(final Context context, final String spName,
                                        final String key) {
        return getSp(context, spName).getString(key, "");
    }

    /**
     * 存int值
     *
     * @param context context
     * @param spName  存放的sp的名字
     * @param key     存的key
     * @param value   存的值
     */
    public static void setValue(final Context context, final String spName, final String key,
                                final int value) {
        getSp(context, spName).edit().putInt(key, value).apply();
    }

    /**
     * 获取int的值
     *
     * @param context context
     * @param spName  获取的sp的名字
     * @param key     key
     * @return value
     */
    public static int getIntValue(final Context context, final String spName,
                                  final String key) {
        return getSp(context, spName).getInt(key, -1);
    }

    /**
     * 存boolean值
     *
     * @param context context
     * @param spName  存放的sp的名字
     * @param key     存的key
     * @param value   存的值
     */
    public static void setValue(final Context context, final String spName, final String key,
                                final boolean value) {
        getSp(context, spName).edit().putBoolean(key, value).apply();
    }

    /**
     * 获取boolean的值
     *
     * @param context context
     * @param spName  获取的sp的名字
     * @param key     key
     * @return value
     */
    public static Boolean getBooleanValue(final Context context, final String spName,
                                          final String key) {
        return getSp(context, spName).getBoolean(key, false);
    }
}
