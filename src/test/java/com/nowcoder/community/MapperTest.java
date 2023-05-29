package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 14:56
 * @Slogn 致未来的你！
 */
@SpringBootTest
@ContextConfiguration(classes = {CommunityApplication.class})
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void test1(){
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setUsername("测试");
        user.setPassword("123");
        user.setSalt("abc");
        user.setEmail("123@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int i = userMapper.updateUser(150, 1);
        System.out.println(i);
    }

    @Test
    public void testSelectDiscussPosts(){
        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(0, 0, 10);
        discussPostList.stream().forEach(System.out::println);

        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);
    }
}
