package com.haichenyi.aloe.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @Title: CrashHandler
 * @Description: 异常捕获
 * CrashHandlerActivity.getInstance().init(this);
 * @Author: wz
 * @Date: 2018/6/1
 * @Version: V1.0
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 文件名:FILE_NAME+FILE_NAME_TIME+FILE_NAME_SUFFIX
     */
    public static final String FILE_NAME = "crash";

    /**
     * 文件名称中间的时间格式
     */
    public static final String FILE_NAME_TIME = "yyyy-MM-dd";
    /**
     * 文件名后缀
     */
    public static final String FILE_NAME_SUFFIX = ".trace";
    /**
     * 一条crash结束的标记
     */
    public static final String CRASH_END_FLAG = "************************************************************";
    /**
     * 上下文
     */
    private Context mContext;

    private static CrashHandler instance;

    private String folderPath = "";
    private String folderName = "crash";
    /**
     * 奔溃是否需要重启
     */
    private boolean isRestart = false;
    private Intent intent;

    /**
     * 崩溃重启
     *
     * @param restart 是否需要重启
     * @param intent  需要启动的intent
     * @return this
     */
    public CrashHandler setRestartApp(boolean restart, Intent intent) {
        isRestart = restart;
        this.intent = intent;
        return this;
    }

    /**
     * 是否需要删除遥远的日志，要在init之后调用
     *
     * @param deleteDistant 是否需要删除
     * @param day           只保留的天数，大于这个天数的都删除
     * @return boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean setDeleteDistant(boolean deleteDistant, int day) {
        return deleteDistant && deleteCrashFile(day);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean deleteCrashFile(int day) {
        String path = folderPath + File.separator + folderName;
        if (FileUtils.isExit(path)) {
            File file = new File(path);
            //按照创建的时间顺序排序的，所以，每次只用删除前面的就可以了
            if (file.isDirectory() && file.listFiles().length > day) {
                for (int i = 0; i < file.listFiles().length - day; i++) {
                    boolean delete = file.listFiles()[i].delete();
                    if (!delete) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
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
            this.folderPath = context.getFilesDir().getAbsolutePath();
        } else {
            this.folderPath = folderPath;
        }
        if (!StringUtils.isEmpty(folderPath)) {
            this.folderName = folderName;
        }
        return this;
    }

    public CrashHandler init(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        folderPath = context.getFilesDir().getAbsolutePath();
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
            //重新启动
            mContext.startActivity(intent);
        }
        //杀死进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * 出现异常，存储到本地
     *
     * @param ex 异常
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        // 储存下载文件的目录
        String savePath = FileUtils.createFileDirs(folderPath, folderName).getAbsolutePath();
        try {
            //以当前时间创建log文件
            String fileLogName = FILE_NAME + DateUtils.getDate(FILE_NAME_TIME, System.currentTimeMillis()) + FILE_NAME_SUFFIX;
            String contentBefore = "";
            //之前有文件
            if (FileUtils.isNoEmpty(savePath, fileLogName)) {
                contentBefore = FileUtils.readFile(savePath + File.separator + fileLogName)
                        .replace("\t", "\n\t");
            }
            File file = FileUtils.createNewFile(savePath, fileLogName);
            String temp = "temp.txt";
            File fileTemp = FileUtils.createNewFile(savePath, temp);
            if (null == fileTemp) {
                return;
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileTemp)));
            //导出手机信息和异常信息
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            BufferedWriter bw = null;
            try {
                if (null == file) {
                    return;
                }
                bw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
                bw.write("发生异常时间：" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()) + "\t\n");
                bw.write("应用版本：" + pi.versionName + "\t\n");
                bw.write("应用版本号：" + pi.versionCode + "\t\n");
                bw.write("android版本号：" + Build.VERSION.RELEASE + "\t\n");
                bw.write("android版本号API：" + Build.VERSION.SDK_INT + "\t\n");
                bw.write("手机制造商:" + Build.MANUFACTURER + "\t\n");
                bw.write("手机型号：" + Build.MODEL + "\t\n");
                ex.printStackTrace(pw);
                ex.printStackTrace();
                //关闭输出流
                pw.close();
                bw.write("异常：" + FileUtils.readFile(fileTemp.getAbsolutePath())
                        .replace("\t", "\n\t")
                        .replace("Caused by", "\n\tCaused by") + "\t\n");
                bw.write(CRASH_END_FLAG + "\t\n");
                bw.write(contentBefore);
                fileTemp.delete();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取crash文件的map
     *
     * @param path crash的文件夹目录
     * @return map：以时间为key，时间格式：{@link CrashHandler#FILE_NAME_TIME}
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayMap<String, File> getFileMap(String path) {
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
    private synchronized String readCrashFile(File file, String endFlag, int max) {
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

    /**
     * 读某一天的crash文件
     *
     * @param date 时间，格式{@link CrashHandler#FILE_NAME_TIME}
     * @param max  crash条数
     * @return String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public synchronized String readCrashFile(String date, int max) {
        String path = folderPath + File.separator + folderName;
        ArrayMap<String, File> fileMap = getFileMap(path);
        File file = fileMap.get(date);
        if (file != null && file.exists()) {
            return readCrashFile(file, CrashHandler.CRASH_END_FLAG, max);
        } else {
            return "";
        }
    }
}
