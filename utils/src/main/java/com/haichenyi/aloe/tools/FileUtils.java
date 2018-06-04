package com.haichenyi.aloe.tools;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @Title: FileUtils
 * @Description: 文件工具类
 * Environment.getDataDirectory():     /data
 * Environment.getRootDirectory():    /system
 * Environment.getDownloadCacheDirectory():     /cache
 * Environment.getExternalStorageDirectory():   /storage/emulated/0
 * @Author: wz
 * @Date: 2018/5/23
 * @Version: V1.0
 */
public class FileUtils {
    /**
     * 在path目录下面创建名称为name的文件夹.
     *
     * @param path 目录
     * @param name 名称
     * @return File
     */
    @SuppressWarnings("all")
    public static File createFileDirs(String path, String name) {
        return createFileDirs(path + File.separator + name);
    }

    @SuppressWarnings("all")
    public static File createFileDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 在path目录下面新建一个名称为name的文件,存在就会删除.
     *
     * @param path 目录
     * @param name 名称（包括文件后缀）
     * @return File
     */
    @SuppressWarnings("all")
    public static File createNewFile(String path, String name) throws IOException {
        File file = new File(path + File.separator + name);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    /**
     * 在path目录下面创建一个名称为name的文件，不存在才会创建.
     *
     * @param path 目录
     * @param name 名称（包括文件后缀）
     * @return File
     */
    @SuppressWarnings("all")
    public static File createFile(String path, String name) throws IOException {
        File file = new File(path + File.separator + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 删除文件夹下的所有内容.
     *
     * @param path 文件夹路径
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        deleteDirFile(dir);
    }

    @SuppressWarnings("all")
    private static void deleteDirFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete(); // 删除所有文件
            } else if (file.isDirectory())
                deleteDirFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 读文件（任意路径）.
     *
     * @param filePath 文件路径
     * @return String
     */
    public static String readFile(String filePath) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(new File(filePath)));
            String result = "";
            while ((result = br.readLine()) != null) {
                sb.append(result);
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
        return sb.toString();
    }

    /**
     * 覆盖方式写文件（任意路径）.
     *
     * @param filePath 文件路径
     * @param msg      需要写的内容
     */
    public static void writeFile(String filePath, String msg) {
        FileOutputStream outStream;
        File file = new File(filePath);
        try {
            outStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            return;
        }
        try {
            outStream.write(msg.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 以追加的方式写文件（任意路径）.
     *
     * @param file 文件
     * @param msg  内容
     */
    public static void writeFileAppend(File file, String msg) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            bw.write(msg);
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
    }

    /**
     * 写文件，openFileOutput方式（目录限制如下）.
     * 写的文件位置：data/data/app的包名/files/自命名文件，不需要申请内存读写权限
     *
     * @param context  context
     * @param fileName 文件名称
     * @param msg      需要写的内容
     * @param type     写文件的方式：覆盖：Context.MODE_PRIVATE。追加：Context.MODE_APPEND
     */
    public static void writeFile(Context context, String fileName, String msg, int type) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput(fileName, type);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读文件，openFileInput方式（目录限制如下）.
     * 读的文件位置：data/data/app的包名/files/自命名文件，不需要申请内存读写权限
     *
     * @param context  context
     * @param fileName 文件名称
     * @return String
     */
    private String readFile(Context context, String fileName) {
        FileInputStream in = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            in = context.openFileInput(fileName);
            br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
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
        return sb.toString();
    }

    /**
     * 判断文件是否为空，文件不存在即为空
     *
     * @param path 目录
     * @param name 文件名称
     * @return boolean
     */
    public static boolean isNoEmpty(String path, String name) {
        return isNoEmpty(path + File.separator + name);
    }

    public static boolean isNoEmpty(String pathAndName) {
        File file = new File(pathAndName);
        return file.exists() && file.length() != 0;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 目录
     * @param name 文件名称
     * @return boolean
     */
    public static boolean isExit(String path, String name) {
        return isExit(path + File.separator + name);
    }

    public static boolean isExit(String pathAndName) {
        return new File(pathAndName).exists();
    }

    /**
     * 解压zip文件
     *
     * @param zipFile   需要解压的zip文件路径
     * @param targetDir 需要解压到的目标文件夹
     */
    public static void Unzip(String zipFile, String targetDir) {
        try {
            FileInputStream fis = new FileInputStream(zipFile);
            Unzip(fis, targetDir);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void Unzip(InputStream inputStream, String targetDir) {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称
        try {
            BufferedOutputStream dest = null; //缓冲输出流
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry entry; //每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();
                    File entryFile = new File(targetDir + File.separator + strEntry);
                    if (entryFile.isFile() && !entryFile.exists()) {
                        entryFile.createNewFile();
                    }
                    File entryDir = new File(entryFile.getParent());
                    if (entryDir.isDirectory() && !entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }

    /**
     * 文件夹拷贝
     *
     * @param fromFile 需要拷贝的文件夹
     * @param toFile   目标文件夹
     * @return int 0：正常 -1：异常
     */
    public static int copyFileDirs(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //创建目标目录
        createFileDirs(toFile);
        //遍历要复制该目录下的全部文件
        for (File currentFile : currentFiles) {
            //如果当前项为子目录 进行递归
            if (currentFile.isDirectory()) {
                copyFileDirs(currentFile.getPath() + File.separator, toFile + currentFile.getName() + File.separator);
            } else {
                //如果当前项为文件则进行文件拷贝
                CopySdcardFile(currentFile.getPath(), toFile + currentFile.getName());
            }
        }
        return 0;
    }

    /**
     * 文件拷贝,要复制的目录下的所有非子目录(文件夹)文件拷贝
     *
     * @param fromFile 需要复制的文件（带文件名称）
     * @param toFile   目标文件（带文件名称）
     * @return int 0：正常 -1：异常
     */
    public static int CopySdcardFile(String fromFile, String toFile) {
        try {
            InputStream inputStream = new FileInputStream(fromFile);
            OutputStream outputStream = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c);
            }
            inputStream.close();
            outputStream.close();
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }
}
