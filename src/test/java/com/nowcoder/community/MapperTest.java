package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 14:56
 * @Slogn 致未来的你！
 */
@SpringBootTest
//@ContextConfiguration(classes = {CommunityApplication.class})
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;
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
        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(0, 0, 10,0);
        discussPostList.stream().forEach(System.out::println);

        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 *10));

        int i = loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        int abc = loginTicketMapper.updateStatus("abc", 1);
        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket1);
    }
    @Test
    public void testSelectTicketByUserId(){
        int i = loginTicketMapper.selectTicketByUserId(151);
        System.out.println(i);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        list.forEach(System.out::println);

        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);

        List<Message> list1 = messageMapper.selectLetters("111_112", 0, 10);
        list1.forEach(System.out::println);

        int i1 = messageMapper.selectLetterCount("111_112");
        System.out.println(i1);

        int i2 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(i2);
    }
}
