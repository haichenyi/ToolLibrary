package com.ule.haichenyi.myapplication;

import android.app.Application;
import android.content.Intent;

import com.haichenyi.aloe.tools.CrashHandler;
import com.haichenyi.aloe.tools.GlideUtils;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.ToastUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsNeedLog(true);
        ToastUtils.init(this);
        GlideUtils.init(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        CrashHandler.getInstance().init(this).setRestartApp(true, intent);
    }
}
