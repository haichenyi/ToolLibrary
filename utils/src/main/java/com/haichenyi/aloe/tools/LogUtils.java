package com.haichenyi.aloe.tools;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import io.reactivex.annotations.NonNull;

/**
 * @Title: LogUtils
 * @Description: 日志打印类，调用之前先初始化是否需要打印，只需要初始化一次
 * 调用setIsNeedLog()方法，true：打印.false 不打印。
 * 调用LogUtils.i()，LogUtils.e()方法打印
 * @Author: wz
 * @Date: 2018/5/17
 * @Version: V1.0
 */
public final class LogUtils {
    public static final String TAG_Wz = "wz";
    public static final String TAG_Aloe = "aloe";
    private static final int V = Log.VERBOSE;
    private static final int D = Log.DEBUG;
    private static final int I = Log.INFO;
    private static final int W = Log.WARN;
    private static final int E = Log.ERROR;
    private static final int A = Log.ASSERT;
    private static int sConsoleFilter = V;
    private static int sFileFilter = V;

    public static final int FILE = 0X10;
    public static final int JSON = 0x20;
    public static final int XML = 0x30;

    private static final int MAX_LEN = 4000;

    private static boolean sTagIsSpace = true; // log标签是否为空白
    private static boolean sLogHeadSwitch = true; // log头部开关，默认开
    private static boolean sLogBorderSwitch = true; // log边框开关，默认开
    private static boolean sLog2FileSwitch = false;// log写入文件开关，默认关
    private static boolean sLogSwitch = true; // log总开关，默认开
    private static boolean sLog2ConsoleSwitch = true; // logcat是否打印，默认打印

    private static String sGlobalTag = null; // log标签
    private static String defaultDir;   // log默认存储目录
    private static String dir;          // log存储目录

    private static final String NULL = "null";
    private static final String ARGS = "args";
    private static final String NULL_TIPS = "Log with null object.";
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TOP_BORDER = "╔═══════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final String LEFT_BORDER = "║ ";
    private static final String BOTTOM_BORDER = "╚═══════════════════════════════════════════════════════════════════════════════════════════════════";

    private static ExecutorService executor;

    public static boolean isNeedLog = true;
    private static final Format FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ", Locale.getDefault());

    private static final char[] T = new char[]{'V', 'D', 'I', 'W', 'E', 'A'};

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate LogUtils...");
    }

    public static void v(final Object content) {
        log(V, sGlobalTag, content);
    }

    public static void v(final String tag, final Object... content) {
        log(V, tag, content);
    }

    public static void d(final Object content) {
        log(D, sGlobalTag, content);
    }

    public static void d(final String tag, final Object... content) {
        log(D, tag, content);
    }

    public static void i(final Object content) {
        log(I, sGlobalTag, content);
    }

    public static void i(final String tag, final Object... content) {
        log(I, tag, content);
    }

    public static void w(final Object content) {
        log(W, sGlobalTag, content);
    }

    public static void w(final String tag, final Object... content) {
        log(W, tag, content);
    }

    public static void e(final Object content) {
        log(E, sGlobalTag, content);
    }

    public static void e(final String tag, final Object... content) {
        log(E, tag, content);
    }

    public static void a(final Object content) {
        log(A, sGlobalTag, content);
    }

    public static void a(final String tag, final Object... content) {
        log(A, tag, content);
    }

    public static void file(final Object content) {
        log(FILE | D, sGlobalTag, content);
    }

    public static void file(@Type final int type, final Object... content) {
        log(FILE | type, sGlobalTag, content);
    }

    public static void file(final String tag, final Object content) {
        log(FILE | D, tag, content);
    }

    public static void file(@Type final int type, final String tag, final Object... content) {
        log(FILE | type, tag, content);
    }

    public static void json(final Object content) {
        log(JSON | D, sGlobalTag, content);
    }

    public static void json(@Type final int type, final Object... content) {
        log(JSON | type, sGlobalTag, content);
    }

    public static void json(final String tag, final Object content) {
        log(JSON | D, tag, content);
    }

    public static void json(@Type final int type, final String tag, final Object... content) {
        log(JSON | type, tag, content);
    }

    public static void xml(final Object content) {
        log(XML | D, sGlobalTag, content);
    }

    public static void xml(@Type final int type, final Object... content) {
        log(XML | type, sGlobalTag, content);
    }

    public static void xml(final String tag, final Object content) {
        log(XML | D, tag, content);
    }

    public static void xml(@Type final int type, final String tag, final Object... content) {
        log(XML | type, tag, content);
    }

    private static void log(final int type, final String tag, final Object... content) {
        if (!isNeedLog) {
            return;
        }
        if (!sLogSwitch || (!sLog2ConsoleSwitch && !sLog2FileSwitch)) {
            return;
        }
        int typeLow = type & 0x0F;
        int typeHigh = type & 0xF0;
        if (typeLow < sConsoleFilter && typeLow < sFileFilter) {
            return;
        }
        final String[] tagAndHead = processTagAndHead(tag);
        String body = processBody(typeHigh, content);
        if (sLog2ConsoleSwitch && typeLow >= sConsoleFilter) {
            print2Console(typeLow, tagAndHead[0], tagAndHead[1] + body);
        }
        if (sLog2FileSwitch || typeHigh == FILE) {
            if (typeLow >= sFileFilter) {
                print2File(typeLow, tagAndHead[0], tagAndHead[2] + body);
            }
        }
    }

    private static String[] processTagAndHead(String tag) {
        if (!sTagIsSpace && !sLogHeadSwitch) {
            tag = sGlobalTag;
        } else {
            StackTraceElement targetElement = new Throwable().getStackTrace()[3];
            String className = targetElement.getClassName();
            String[] classNameInfo = className.split("\\.");
            if (classNameInfo.length > 0) {
                className = classNameInfo[classNameInfo.length - 1];
            }
            if (className.contains("$")) {
                className = className.split("\\$")[0];
            }
            if (sTagIsSpace) {
                tag = isSpace(tag) ? className : tag;
            }
            if (sLogHeadSwitch) {
                String head = new Formatter().format("%s(%s.java:%d)", targetElement.getMethodName(),
                        className, targetElement.getLineNumber()).toString();
                return new String[]{tag, head + LINE_SEP, " [" + head + "]: "};
            }
        }
        return new String[]{tag, "", ": "};
    }

    private static boolean isSpace(String tag) {
        if (null == tag) {
            return true;
        }
        for (int i = 0; i < tag.length(); i++) {
            if (!Character.isWhitespace(tag.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String processBody(final int type, final Object... content) {
        String body = NULL_TIPS;
        if (null != content) {
            if (content.length == 1) {
                Object object = content[0];
                body = null == object ? NULL : object.toString();
                if (type == JSON) {
                    body = formatJson(body);
                } else if (type == XML) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < content.length; i++) {
                    Object obj = content[i];
                    sb.append(ARGS).append("[").append(i).append("]").append(" = ")
                            .append(obj == null ? NULL : obj.toString()).append(LINE_SEP);
                }
                body = sb.toString();
            }
        }
        return body;
    }

    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        Source xmlInput = new StreamSource(new StringReader(xml));
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static void print2Console(final int type, final String tag, String msg) {
        if (sLogBorderSwitch) {
            print(type, tag, TOP_BORDER);
            msg = addLeftBorder(msg);
        }
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            print(type, tag, msg.substring(0, MAX_LEN));
            String sub;
            int index = MAX_LEN;
            for (int i = 0; i < countOfSub; i++) {
                sub = msg.substring(index, Math.min(msg.length(), index + MAX_LEN));
                print(type, tag, sLogBorderSwitch ? LEFT_BORDER + sub : sub);
                if (index + MAX_LEN > msg.length()) {
                    break;
                }
                index += MAX_LEN;
            }
            sub = msg.substring(index, len);
            print(type, tag, sLogBorderSwitch ? LEFT_BORDER + sub : sub);
        } else {
            print(type, tag, msg);
        }
        if (sLogBorderSwitch) {
            print(type, tag, BOTTOM_BORDER);
        }
    }

    private static String addLeftBorder(final String msg) {
        if (!sLogBorderSwitch) {
            return msg;
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = msg.split(LINE_SEP);
        for (String line : lines) {
            sb.append(LEFT_BORDER).append(line).append(LINE_SEP);
        }
        return sb.toString();
    }

    private static void print(final int type, final String tag, final String msg) {
        Log.println(type, tag, msg);
    }

    private static void print2File(final int type, final String tag, final String msg) {
        Date now = new Date(System.currentTimeMillis());
        String format = FORMAT.format(now);
        String date = format.substring(0, 5);
        String time = format.substring(6);
        final String fullPath = (dir == null ? defaultDir : dir) + date + ".txt";
        if (!createOrExistsFile(fullPath)) {
            Log.e(tag, "log to " + fullPath + "failed!");
            return;
        }
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(time).append(T[type - V])
                .append("/")
                .append(tag)
                .append(msg)
                .append(LINE_SEP);
        final String content = sb.toString();
        synchronized (LogUtils.class) {
            if (executor == null) {
                executor = Executors.newSingleThreadExecutor();
            }
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(fullPath, true));
                    bw.write(content);
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private static boolean createOrExistsFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 数据压缩.
     *
     * @param input 数据
     * @return 压缩后的数据
     */
    public static byte[] compress(final byte[] input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(1);
        compressor.setInput(input);
        compressor.finish();
        final byte[] buf = new byte[2048];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        compressor.end();
        return bos.toByteArray();
    }

    /**
     * 数据解压.
     *
     * @param input 数据
     * @return 解压后的数据
     */
    public static byte[] uncompress(final byte[] input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater compressor = new Inflater();
        compressor.setInput(input);
        final byte[] buf = new byte[2048];
        try {
            while (!compressor.finished()) {
                int count = compressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
        } finally {
            compressor.end();
        }
        return bos.toByteArray();
    }

    /**
     * Config.
     */
    private static class Config {
        private Config(@NonNull Context context) {
            if (defaultDir != null) {
                return;
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && context.getExternalCacheDir() != null) {
                defaultDir = context.getExternalCacheDir() + FILE_SEP + "log" + FILE_SEP;
            } else {
                defaultDir = context.getCacheDir() + FILE_SEP + "log" + FILE_SEP;
            }
        }

        public Config setLogSwitch(final boolean logSwitch) {
            LogUtils.sLogSwitch = logSwitch;
            return this;
        }

        public Config setConsoleSwitch(final boolean consoleSwitch) {
            LogUtils.sLog2ConsoleSwitch = consoleSwitch;
            return this;
        }

        /**
         * 设置日志标记.
         *
         * @param tag 标记
         * @return Config
         */
        public Config setGlobalTag(final String tag) {
            if (isSpace(tag)) {
                LogUtils.sGlobalTag = "";
                sTagIsSpace = true;
            } else {
                LogUtils.sGlobalTag = tag;
                sTagIsSpace = false;
            }
            return this;
        }

        public Config setLogHeadSwitch(final boolean logHeadSwitch) {
            LogUtils.sLogHeadSwitch = logHeadSwitch;
            return this;
        }

        public Config setLog2FileSwitch(final boolean log2FileSwitch) {
            LogUtils.sLog2FileSwitch = log2FileSwitch;
            return this;
        }

        /**
         * 设置日志目录.
         *
         * @param dir 目录
         * @return Config
         */
        public Config setDir(final String dir) {
            if (isSpace(dir)) {
                LogUtils.dir = null;
            } else {
                LogUtils.dir = dir.endsWith(FILE_SEP) ? dir : dir + FILE_SEP;
            }
            return this;
        }

        public Config setDir(final File dir) {
            LogUtils.dir = dir == null ? null : dir.getAbsolutePath() + FILE_SEP;
            return this;
        }

        public Config setBorderSwitch(final boolean borderSwitch) {
            LogUtils.sLogBorderSwitch = borderSwitch;
            return this;
        }

        public Config setConsoleFilter(@Type final int consoleFilter) {
            LogUtils.sConsoleFilter = consoleFilter;
            return this;
        }

        public Config setFileFilter(@Type final int fileFilter) {
            LogUtils.sFileFilter = fileFilter;
            return this;
        }

        @Override
        public String toString() {
            return "switch: " + sLogSwitch
                    + LINE_SEP + "console: " + sLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (sTagIsSpace ? "null" : sGlobalTag)
                    + LINE_SEP + "head: " + sLogHeadSwitch
                    + LINE_SEP + "file: " + sLog2FileSwitch
                    + LINE_SEP + "dir: " + (dir == null ? defaultDir : dir)
                    + LINE_SEP + "border: " + sLogBorderSwitch
                    + LINE_SEP + "consoleFilter: " + T[sConsoleFilter - V]
                    + LINE_SEP + "fileFilter: " + T[sFileFilter - V];
        }
    }

    @IntDef({V, D, I, W, E, A})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Type {
    }

    /**
     * 是否需要日志
     * @param isNeedLog true：需要。false：不需要
     */
    public static void setIsNeedLog(boolean isNeedLog) {
        LogUtils.isNeedLog = isNeedLog;
    }
}
