package com.acuitybotting.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 6/28/2018.
 */
@Slf4j
public class ExecutorUtil {

    public static ScheduledThreadPoolExecutor newScheduledExecutorPool(int size){
        return new ScheduledThreadPoolExecutor(size){
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t != null){
                    log.error("Error in thread pool.", t);
                }
            }
        };
    }

    public static ExecutorService newExecutorPool(int size){
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()){
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t != null){
                    log.error("Error in thread pool.", t);
                }
            }
        };
    }

    public static void run(int poolSize, Consumer<ExecutorService> executorConsumer){
        ExecutorService executor = newExecutorPool(poolSize);
        executorConsumer.accept(executor);
        executor.shutdown();
        try {
            executor.awaitTermination(3, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("Error in thread pool.", e);
        }
    }
}
