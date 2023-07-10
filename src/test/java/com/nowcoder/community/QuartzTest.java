package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author Szw 2001
 * @Date 2023/7/5 19:44
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class QuartzTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteJob(){
        try {
            System.out.println(scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup")));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
