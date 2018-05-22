package com.ule.haichenyi.myapplication;

import android.app.Application;

import com.haichenyi.aloe.tools.GlideUtils;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.ToastUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsNeedLog(true);
        ToastUtils.init(this);
        GlideUtils.init(R.mipmap.ic_launcher,R.mipmap.ic_launcher_round);
    }
}
