package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @Author Szw 2001
 * @Date 2023/6/12 16:36
 * @Slogn 致未来的你！
 */
@SpringBootTest
//@ContextConfiguration(classes = {CommunityApplication.class})
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "附近的饭店啦赌博,四分零六嫖娼,力扣开票,开开票0开票123";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }


}
