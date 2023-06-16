package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import com.sun.jndi.cosnaming.CNCtx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @Author Szw 2001
 * @Date 2023/5/30 17:30
 * @Slogn 致未来的你！
 */
@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("1837862986@qq.com","今天你敲代码了吗？","看什么？还不去敲代码！！！");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","测试人员ABC");
        String s = templateEngine.process("/mail/demo", context);
        System.out.println(s);
        mailClient.sendMail("song3516@qq.com","html",s);
    }
}
