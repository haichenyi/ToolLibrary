package com.ule.haichenyi.myapplication;

import android.app.Application;
import android.content.Intent;

import com.haichenyi.aloe.tools.FileUtils;
import com.haichenyi.aloe.tools.GlideUtils;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.ToastUtils;

import java.io.File;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsNeedLog(true);
        ToastUtils.init(this);
        GlideUtils.init(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round);

        String path = getFilesDir().getAbsolutePath() + File.separator + "log_crash";
        if (FileUtils.isExit(path)) {
            File file = new File(path);
            //按照创建的时间顺序排序的，所以，每次只用删除第一个就可以了
            if (file.isDirectory() && file.listFiles().length > 10) {
                for (int i = 0; i < file.listFiles().length - 10; i++) {
                    file.listFiles()[i].delete();
                }
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        CrashHandler.getInstance().init(this).setRestartApp(true, intent);
    }
}
