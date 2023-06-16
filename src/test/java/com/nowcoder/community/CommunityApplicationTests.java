package com.nowcoder.community;

import com.nowcoder.community.dao.TestDao;
import com.nowcoder.community.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
//@ContextConfiguration(classes = {CommunityApplication.class})
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void test1()
    {
        System.out.println(applicationContext);
        TestDao bean = applicationContext.getBean(TestDao.class);
        System.out.println(bean.select());
    }

    @Test
    public void test2(){
        TestService bean = applicationContext.getBean(TestService.class);
        System.out.println(bean);
    }

}
