package keldkemp.telegram.configs;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ExecutorConfiguration {

    @Bean("telegramHandlerThread")
    public ExecutorService executorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("telegram-%d").build();
        return Executors.newFixedThreadPool(5, threadFactory);
    }

    @Bean("telegramSchedulerThread")
    public ExecutorService telegramSchedulerThread() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("tg-scheduler-%d").build();
        return Executors.newFixedThreadPool(10, threadFactory);
    }
}
