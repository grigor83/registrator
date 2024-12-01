package com.example.registar.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorHelper {
    public static ExecutorService executor;

    public static ExecutorService getExecutor(){
        if (null == executor)
            executor = createExecutorInstance();

        return executor;
    }

    private static ExecutorService createExecutorInstance(){
        executor = Executors.newFixedThreadPool(7);
        return executor;
    }
}
