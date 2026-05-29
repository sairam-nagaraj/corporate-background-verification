package io.github.four88labs.corporateBackgroundVerification.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Configuration
public class ThreadPoolExecutorBean {
    Logger log = Logger.getLogger(ThreadPoolExecutorBean.class.getName());
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MyAsyncThread-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.warning("Task rejected, thread pool is full and queue is also full"));
        executor.initialize();
        return executor;
    }
}
