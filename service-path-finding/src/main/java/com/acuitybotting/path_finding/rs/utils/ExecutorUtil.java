package com.acuitybotting.path_finding.rs.utils;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 6/28/2018.
 */
public class ExecutorUtil {

    public static void run(int poolSize, Consumer<Executor> executorConsumer){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()){
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t != null){
                    t.printStackTrace();
                }
            }
        };
        executorConsumer.accept(threadPoolExecutor);
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(3, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
