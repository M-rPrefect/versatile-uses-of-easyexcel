package org.excel_demo.excel_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Chen和斌
 */
@Configuration
public class ExcelThreadPoolConfig {

    /**
     * 线程池配置
     */
    @Value("${thread.pool.core.size}")
    private int corePoolSize;

    /**
     * 线程池最大数量
     */
    @Value("${thread.pool.max.size}")
    private int maxPoolSize;

    /**
     * 线程池队列容量
     */
    @Value("${thread.pool.queue.capacity}")
    private int queueCapacity;

    /**
     * 线程池空闲存活时间
     */
    @Value("${thread.pool.keep.alive.seconds}")
    private int keepAliveSeconds;


    @Bean
    public ThreadPoolExecutor excelThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize, // 线程池核心线程数
                maxPoolSize, // 线程池最大线程数
                keepAliveSeconds, // 线程池空闲存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(queueCapacity),  // 线程池队列
                Executors.defaultThreadFactory(), // 线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );
    }

//    @Bean(name = "excelThreadPool")
//    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // 核心线程数
//        executor.setMaxPoolSize(10); // 最大线程数
//        executor.setQueueCapacity(20); // 队列容量
//        executor.setKeepAliveSeconds(30); // 线程存活时间
//        executor.setThreadNamePrefix("excel-custom-thread-"); // 线程名前缀
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略
//        executor.initialize(); // 如果手动管理则需要调用
//        return executor;
//    }
}
