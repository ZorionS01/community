package com.nowcoder.community;

import com.nowcoder.community.service.TestService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author Szw 2001
 * @Date 2023/7/5 16:19
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //JDK普通线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private TestService testService;

    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //JDK普通线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(10000);
    }

    //JDK定时任务线程池
    @Test
    public void testScheduleExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduleExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    //Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testThreadPoolTaskExecutor ");
            }
        };

        for (int i = 0; i < 10 ; i++) {
            threadPoolTaskExecutor.submit(task);
        }

        sleep(10000);
    }

    //Spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskScheduler");
            }
        };

        LocalDateTime localDateTime = LocalDateTime.now().plus(10000, ChronoUnit.MILLIS);
        threadPoolTaskScheduler.scheduleAtFixedRate(task,
                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()),1000);
        sleep(30000);
    }

    //Spring普通线程池（简化）
    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for (int i = 0; i < 10 ; i++) {
            testService.executel();
        }

        sleep(10000);
    }

    //Spring定时任务线程池（简化）
    @Test
    public void testThreadPoolTaskSchedulerSimple(){
        sleep(30000);
    }

}
