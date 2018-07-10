package com.acuitybotting.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 6/28/2018.
 */
@Slf4j
public class ExecutorUtil {

    private static Consumer<Throwable> defaultConsumer = throwable -> log.error("Error in thread pool.", throwable);

    public static ScheduledThreadPoolExecutor newScheduledExecutorPool(int size) {
        return newScheduledExecutorPool(size, defaultConsumer);
    }

    public static ScheduledThreadPoolExecutor newScheduledExecutorPool(int size, Consumer<Throwable> exceptionHandler) {
        return new ScheduledThreadPoolExecutor(size) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                if (t != null) {
                    exceptionHandler.accept(t);
                }
                super.afterExecute(r, t);
            }
        };
    }

    public static ExecutorService newExecutorPool(int size) {
        return newExecutorPool(size, defaultConsumer);
    }

    public static ExecutorService newExecutorPool(int size, Consumer<Throwable> exceptionHandler) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t != null) {
                    exceptionHandler.accept(t);
                }
            }
        };
    }

    public static void run(int poolSize, Consumer<ExecutorService> executorConsumer) {
        run(poolSize, executorConsumer, defaultConsumer);
    }

    public static void run(int poolSize, Consumer<ExecutorService> executorConsumer, Consumer<Throwable> exceptionHandler) {
        ExecutorService executor = newExecutorPool(poolSize, exceptionHandler);
        executorConsumer.accept(executor);
        executor.shutdown();
        try {
            executor.awaitTermination(3, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

