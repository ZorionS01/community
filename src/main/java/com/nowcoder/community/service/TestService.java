package com.nowcoder.community.service;

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
}
