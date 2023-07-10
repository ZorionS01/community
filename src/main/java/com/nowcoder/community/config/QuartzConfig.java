package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @Author Szw 2001
 * @Date 2023/7/5 19:12
 * @Slogn 致未来的你！
 */
//该配置作用只有第一次读取会有用 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    //FactorBean可简化Bean的实例化过程:
    //1.通过FactorBean封装了Bean的实例化过程
    //2.将FactorBean装配到Spring容器里
    //3.将FactoryBean注入其他的bean
    //4.该bean得到的是factoryBean所管理的对象实例

    //配置JobDetail
//    @Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(AlphaJob.class);
        jobDetailFactoryBean.setName("alphaJob");
        jobDetailFactoryBean.setGroup("alphaJobGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

    //配置Trigger(SimpleTriggerFactoryBean,CronTriggerFactoryBean)
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(alphaJobDetail);
        simpleTriggerFactoryBean.setName("alphaTrigger");
        simpleTriggerFactoryBean.setGroup("alphaTriggerGroup");
        simpleTriggerFactoryBean.setRepeatInterval(3000);
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());
        return simpleTriggerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(PostScoreRefreshJob.class);
        jobDetailFactoryBean.setName("postScoreRefreshJob");
        jobDetailFactoryBean.setGroup("communityJobGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(postScoreRefreshJobDetail);
        simpleTriggerFactoryBean.setName("postScoreRefreshTrigger");
        simpleTriggerFactoryBean.setGroup("communityTriggerGroup");
        simpleTriggerFactoryBean.setRepeatInterval(1000*60*5);
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());
        return simpleTriggerFactoryBean;
    }

}
