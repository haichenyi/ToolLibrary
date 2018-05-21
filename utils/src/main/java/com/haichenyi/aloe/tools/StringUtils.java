package com.haichenyi.aloe.tools;

import android.text.TextUtils;

/**
 * @Title: StringUtils
 * @Description: 字符串工具类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public class StringUtils {
    /**
     * 判断字符串是否为空
     *
     * @param str str
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1 str1
     * @param str2 str2
     * @return boolean
     */
    public static boolean isEquals(String str1, String str2) {
        return TextUtils.equals(str1, str2);
    }

    /**
     * 16进制字符串转byte数组.
     *
     * @param hexStr 16进制字符串
     * @return byte数组
     */
    public static byte[] hexStr2bytes(String hexStr) {
        hexStr = hexStr.trim().toUpperCase();
        if (!isEmpty(hexStr) && (hexStr.length() & 1) == 0) {
            String regex = "^[A-F0-9]+$";
            if (hexStr.matches(regex)) {
                byte[] bytes = new byte[hexStr.length() / 2];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = (byte) Short.parseShort(hexStr.substring(2 * i, 2 * i + 2), 16);
                }
                return bytes;
            }
        }
        return new byte[0];
    }

    /**
     * byte数组转16进制字符串.
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    public static String byte2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
