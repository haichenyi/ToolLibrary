package com.ule.haichenyi.myapplication;

import android.app.Application;

import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.ToastUtils;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: ${date}
 * @Version: V1.0
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsNeedLog(true);
        ToastUtils.init(this);
    }
}
