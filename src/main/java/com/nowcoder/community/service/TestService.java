package com.nowcoder.community.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author Szw 2001
 * @Date 2023/5/27 18:45
 * @Slogn 致未来的你！
 */
//@Service
public class TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestService.class);


    public TestService(){
        System.out.println("实例化TestService");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化TestService");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁TestService");
    }


    //让该方法在多线程环境下，被异步的调用
    @Async
    public void executel(){
        logger.debug("executel");
    }

    @Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2(){
        logger.debug("execute2");
    }
}
