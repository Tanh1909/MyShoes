package com.example.moduleapp.controller;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Log4j2
public class Test {
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MyThread-");

        // Sử dụng TaskDecorator để log trước và sau khi thread thực hiện xong công việc
        executor.setTaskDecorator(task -> () -> {
            String threadName = Thread.currentThread().getName();
            log.info("Thread {} is about to execute a task", threadName);
            try {
                task.run();
            } finally {
                log.info("Thread {} has finished the task and will return to the pool", threadName);
            }
        });

        executor.initialize();
        return executor;
    }
}
