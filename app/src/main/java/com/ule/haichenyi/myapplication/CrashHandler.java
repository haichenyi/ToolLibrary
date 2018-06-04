package com.ule.haichenyi.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.haichenyi.aloe.tools.DateUtils;
import com.haichenyi.aloe.tools.FileUtils;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @Title: CrashHandler
 * @Description: 异常捕获
 * CrashHandlerActivity.getInstance().init(this);
 * @Author: wz
 * @Date: 2018/6/1
 * @Version: V1.0
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //文件名
    private static final String FILE_NAME = "log";
    //文件名后缀
    private static final String FILE_NAME_SUFFIX = ".txt";
    //上下文
    private Context mContext;

    private static CrashHandler instance;

    private String FOLDER_PATH = "";
    private String FOLDER_NAME = "log_crash";
    private boolean isRestart = false;
    private Intent intent;

    public CrashHandler setRestartApp(boolean restart, Intent intent) {
        isRestart = restart;
        this.intent = intent;
        return this;
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {

        if (null == instance) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 初始化信息
     *
     * @param context    上下文对象
     * @param folderPath 存储目录的路径（不包括名称）
     * @param folderName 存储目录的文件夹名称
     */
    public CrashHandler init(Context context, String folderPath, String folderName) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        if (StringUtils.isEmpty(folderPath)) {
            FOLDER_PATH = context.getFilesDir().getAbsolutePath();
        } else {
            FOLDER_PATH = folderPath;
        }
        if (!StringUtils.isEmpty(folderPath)) {
            FOLDER_NAME = folderName;
        }
        return this;
    }

    public CrashHandler init(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        FOLDER_PATH = context.getFilesDir().getAbsolutePath();
        return this;
    }

    /**
     * 捕获异常的回调
     *
     * @param thread 当前线程
     * @param ex     异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            //导出异常信息到手机
            dumpExceptionToSDCard(ex);
        }
        if (isRestart && intent != null) {
            LogUtils.i(LogUtils.TAG_Wz, "重新启动");
            mContext.startActivity(intent);
        }
        //延时2秒杀死进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * 出现异常，存储到本地
     *
     * @param ex 异常
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        // 储存下载文件的目录
        String savePath = FileUtils.createFileDirs(FOLDER_PATH, FOLDER_NAME).getAbsolutePath();
        try {
            //以当前时间创建log文件
            String fileLogName = FILE_NAME + DateUtils.getDate("yyyy-MM-dd", System.currentTimeMillis()) + FILE_NAME_SUFFIX;
            String contentBefore = "";
            //之前有文件
            if (FileUtils.isNoEmpty(savePath, fileLogName)) {
                contentBefore = FileUtils.readFile(savePath + File.separator + fileLogName)
                        .replace("\t", "\n\t");
            }
            File file = FileUtils.createNewFile(savePath, fileLogName);
            String temp = "temp.txt";
            File fileTemp = FileUtils.createNewFile(savePath, temp);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileTemp)));
            //导出手机信息和异常信息
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            FileUtils.writeFileAppend(file, "发生异常时间：" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()) + "\t\n");
            FileUtils.writeFileAppend(file, "应用版本：" + pi.versionName + "\t\n");
            FileUtils.writeFileAppend(file, "应用版本号：" + pi.versionCode + "\t\n");
            FileUtils.writeFileAppend(file, "android版本号：" + Build.VERSION.RELEASE + "\t\n");
            FileUtils.writeFileAppend(file, "android版本号API：" + Build.VERSION.SDK_INT + "\t\n");
            FileUtils.writeFileAppend(file, "手机制造商:" + Build.MANUFACTURER + "\t\n");
            FileUtils.writeFileAppend(file, "手机型号：" + Build.MODEL + "\t\n");
            ex.printStackTrace(pw);
            ex.printStackTrace();
            //关闭输出流
            pw.close();
            FileUtils.writeFileAppend(file, "异常：" + FileUtils.readFile(fileTemp.getAbsolutePath())
                    .replace("\t", "\n\t")
                    .replace("Caused by", "\n\tCaused by") + "\t\n");
            FileUtils.writeFileAppend(file, "************************************************************\t\n");
            FileUtils.writeFileAppend(file, contentBefore);
            fileTemp.delete();
            LogUtils.i(LogUtils.TAG_Wz, FileUtils.readFile(file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
