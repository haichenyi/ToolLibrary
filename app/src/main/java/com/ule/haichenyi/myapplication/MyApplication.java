package com.ule.haichenyi.myapplication;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;

import com.haichenyi.aloe.tools.CrashHandler;
import com.haichenyi.aloe.tools.DateUtils;
import com.haichenyi.aloe.tools.FileUtils;
import com.haichenyi.aloe.tools.GlideUtils;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: 2018/6/7
 * @Version: V1.0
 */
public class MyApplication extends Application {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsNeedLog(true);
        ToastUtils.init(this);
        GlideUtils.init(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round);
        initCrash();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initCrash() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        CrashHandler.getInstance().init(this).setRestartApp(true, intent)
                .setDeleteDistant(true, 10);
        LogUtils.i(LogUtils.TAG_Wz,CrashHandler.getInstance().readCrashFile(DateUtils.getDate(CrashHandler.FILE_NAME_TIME, System.currentTimeMillis()),10));
        LogUtils.i(LogUtils.TAG_Wz,CrashHandler.getInstance().readCrashFile("2018-06-80",10));
        LogUtils.i(LogUtils.TAG_Wz,CrashHandler.getInstance().readCrashFile("2018-06-15",10));
    }

    /**
     * 获取crash文件的map
     *
     * @param path crash的文件夹目录
     * @return map：以时间为key，时间格式：{@link CrashHandler#FILE_NAME_TIME}
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ArrayMap<String, File> getFileMap(String path) {
        ArrayMap<String, File> mapFile = new ArrayMap<>();
        if (FileUtils.isExit(path)) {
            File file = new File(path);
            if (file.isDirectory() && file.listFiles().length > 0) {
                for (File file1 : file.listFiles()) {
                    String time = file1.getName().substring(CrashHandler.FILE_NAME.length(),
                            file1.getName().indexOf(CrashHandler.FILE_NAME_SUFFIX.charAt(0)));
                    mapFile.put(time, file1);
                }
            }
        }
        return mapFile;
    }

    /**
     * 读crash文件
     *
     * @param file    需要读的文件
     * @param endFlag 一条crash结束的标记{@link CrashHandler#CRASH_END_FLAG}
     * @param max     最大读多少条
     * @return String
     */
    public static synchronized String readCrashFile(File file, String endFlag, int max) {
        int num = 0;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));
            String result;
            while ((result = br.readLine()) != null) {
                sb.append(result);
                if (result.contains(endFlag)) {
                    num++;
                    if (num == max) {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString().replace("\t", "\n\t");
    }
}
