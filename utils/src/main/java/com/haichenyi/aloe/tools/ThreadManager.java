package com.haichenyi.aloe.tools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Title: ThreadManager
 * @Description: 线程管理工具类
 * 用法：ThreadManager.getDefault().execute()
 * @Author: wz
 * @Date: 2018/5/15
 * @Version: V1.0
 */
public class ThreadManager {
    private static final String TAG = ThreadManager.class.getSimpleName();
    private static volatile ThreadManager defaultInstance;
    private static final ThreadManagerBuilder DEFAULT_BUILDER = new ThreadManagerBuilder();
    private static ExecutorService executorService = null;

    public static ThreadManager getDefault() {
        if (defaultInstance == null) {
            synchronized (ThreadManager.class) {
                if (defaultInstance == null) {
                    defaultInstance = new ThreadManager();
                }
            }
        }
        return defaultInstance;
    }

    private ThreadManager() {
        this(DEFAULT_BUILDER);
    }

    private ThreadManager(ThreadManagerBuilder builder) {
        executorService = new ThreadPoolExecutor(builder.corePoolSize, builder.maxPoolSize,
                builder.keepAliveTime, TimeUnit.SECONDS, builder.taskQueue);
    }

    public static ThreadManagerBuilder builder() {
        return new ThreadManagerBuilder();
    }


    /**
     * 执行线程
     *
     * @param runnable Runnable
     */
    public void execute(Runnable runnable) {
        if (executorService != null) {
            executorService.execute(runnable);
        }
    }

    public static class ThreadManagerBuilder {
        /**
         * 核心线程
         */
        private int corePoolSize = 5;
        /**
         * 最大线程
         */
        private int maxPoolSize = 10;
        /**
         * 线程执行完之后的回收时间
         */
        private long keepAliveTime = 2000L;
        /**
         * 排队的三种策略：1、直接提交（SynchronousQueue），允许线程无界增长，最大为maxPoolSize
         * 2、无界队列（LinkedBlockingQueue），允许线程无界增长，maxPoolSize失效，最大为corePoolSize，超出等待
         * 3、有界队列（ArrayBlockingQueue）
         */
        private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue(5);

        public ThreadManagerBuilder setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public ThreadManagerBuilder setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public ThreadManagerBuilder setKeepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public ThreadManagerBuilder setTaskQueue(BlockingQueue<Runnable> taskQueue) {
            this.taskQueue = taskQueue;
            return this;
        }
    }
}
