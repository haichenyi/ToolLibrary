package com.ule.haichenyi.myapplication;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;

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

public class MyApplication extends Application {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsNeedLog(true);
        ToastUtils.init(this);
        GlideUtils.init(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round);
        initCrash();
        String path = getFilesDir().getAbsolutePath() + File.separator + "log_crash";
        ArrayMap<String, File> fileMap = getFileMap(path);
        File file = fileMap.get(DateUtils.getDate(CrashHandler.FILE_NAME_TIME, System.currentTimeMillis()));
        String s = readCrashFile(file, CrashHandler.CRASH_END_FLAG, 10);
        LogUtils.i(LogUtils.TAG_Wz, s);
    }

    private void initCrash() {
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
            String result = "";
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
