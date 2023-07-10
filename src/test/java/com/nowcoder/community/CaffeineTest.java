package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/7/10 9:33
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class CaffeineTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void initDataForTest(){
        for (int i = 0; i < 300000 ; i++) {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setUserId(111);
            discussPost.setTitle("互联网求职暖春计划");
            discussPost.setContent("今年的就业形势，确实不容乐观。过了个年，仿佛跳水一般，整个讨论区哀鸿遍野！19届真的没人要了吗？！18届被优化真的没有出路了吗？！大家的“哀嚎”与“悲惨遭遇”牵动了每日潜伏于讨论区的牛客小哥哥小姐姐们的心，于是牛客决定：是时候为大家做点什么了！为了帮助大家度过“寒冬”，牛客网特别联合60+家企业，开启互联网求职暖春计划，面向18届&19届，拯救0 offer！");
            discussPost.setCreateTime(new Date());
            discussPost.setScore(Math.random()*2000);
            discussPostService.addDiscussPost(discussPost);
        }
    }

    @Test
    public void testCache(){
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,0));
    }
    /**
     * 开启缓存前：
     * 测试一：TPS（吞吐量）：6.7270655888894915/s
     * 测试二：TPS（吞吐量）：6.766612641815235/s
     * 测试三：TPS（吞吐量）：6.899937273297516/s
     * 测试四：TPS（吞吐量）：6.651794216067688/s
     * 测试五：TPS（吞吐量）：7.118933651538367/s
     * 平均值：TPS（吞吐量）：6.8/s
     *
     * 开启缓存后：
     * 测试一：TPS（吞吐量）：186.16186245365577/s
     * 测试二：TPS（吞吐量）：187.58035699377183/s
     * 测试三：TPS（吞吐量）：189.83407689608504/s
     * 测试四：TPS（吞吐量）：188.90469416785206/s
     * 测试五：TPS（吞吐量）：196.45359311403215/s
     * 平均值：TPS（吞吐量）：189.8/s
     * TPS提升了(189.8-6.8)/6.8=27%
     **/

}
