package com.haichenyi.aloe.tools;

import java.util.Arrays;

/**
 * @Title: ByteUtils
 * @Description: 数组工具类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
public class ByteUtils {
    /**
     * byte数组合并.
     *
     * @param first 第一个数组
     * @param other 其它数组
     * @return 合并后的数据
     */
    public static byte[] concatAll(byte[] first, byte[]... other) {
        int totalLength = first.length;
        for (byte[] ts : other) {
            totalLength += ts.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] ts : other) {
            System.arraycopy(ts, 0, result, offset, ts.length);
            offset += ts.length;
        }
        return result;
    }

    /**
     * byte数组复制.
     *
     * @param data   原数组
     * @param index  复制的位置
     * @param length 复制的长度
     * @return 复制后的数组
     */
    public static byte[] copyBytes(byte[] data, int index, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(data, index, bytes, 0, length);
        return bytes;
    }
}
