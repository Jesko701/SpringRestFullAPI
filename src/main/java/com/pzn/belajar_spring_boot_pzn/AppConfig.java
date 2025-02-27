package com.pzn.belajar_spring_boot_pzn;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAsync
@EnableTransactionManagement
public class AppConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int corePoolSize = 100; // Keep enough threads active
        int maxPoolSize = 500; // Allow high concurrency
        int queueCapacity = 10_000; // Large queue to prevent task rejection
        int keepAliveSeconds = 60; // Keep threads alive for reuse

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.initialize();
        return executor;
    }

}
