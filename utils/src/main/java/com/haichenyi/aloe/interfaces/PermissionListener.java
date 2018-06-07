package com.haichenyi.aloe.interfaces;

/**
 * @Title: PermissionListener
 * @Description: 权限回调接口
 * @Author: wz
 * @Date: 2018/5/17
 * @Version: V1.0
 */
public interface PermissionListener {
    /**
     * 权限结果回调
     *
     * @param hasPermission hasPermission
     */
    void onResult(boolean hasPermission);
}
