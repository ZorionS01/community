package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 20:38
 * @Slogn 致未来的你！
 */
@Service
public class UserService implements CommunityConstant {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;



    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }
        //验证账号
        if(userMapper.selectByName(user.getUsername()) != null){
            map.put("usernameMsg","该账号已存在!");
            return map;
        }

        //验证邮箱
        if (userMapper.selectByEmail(user.getEmail()) != null){
            map.put("emailMsg","该邮箱已注册！");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));

        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        //mybatis.configuration.useGeneratedKeys=true insert后id自动回填
        String url = domain + contextPath + "/activation/" +user.getId()
                +"/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg","该账号不存在!");
            return map;
        }
        //验证状态
        if (user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活!");
            return map;
        }

        //验证密码
        String s = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(s)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        //生成登录凭证
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(user.getId());
        ticket.setStatus(0);
        ticket.setTicket(CommunityUtil.generateUUID());
        ticket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        //先删除凭证记录再插入避免同个用户多个凭证
        if(loginTicketMapper.selectTicketByUserId(user.getId())>0){
            loginTicketMapper.deleteTicketByUserId(user.getId());
        }
        loginTicketMapper.insertLoginTicket(ticket);
        map.put("ticket",ticket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    public int updatePassword(int userId,String newPassword){
        return userMapper.updatePassword(userId,newPassword);
    }

    public Map<String,Object> sendVerifyCode(String email){
        User user = userMapper.selectByEmail(email);
        Map<String,Object> map = new HashMap<>();
        //空值判断
        if (StringUtils.isBlank(email)){
            map.put("emailMsg","请输入邮箱！");
            return map;
        }else if (user == null){
            map.put("emailMsg","该邮箱还未注册,请注册后再使用！");
            return map;
        }else if (user.getStatus() == 0){
            map.put("emailMsg","该邮箱还未激活,请到邮箱中激活后使用！");
            return map;
        }else {
            //发送邮件
            Context context = new Context();
            context.setVariable("email",email);
            //生成随机4位数验证码
            String verifyCode = String.valueOf((int) (Math.random() * 9000 + 1000));
            context.setVariable("verifyCode",verifyCode);
            String process = templateEngine.process("/mail/forget", context);
            mailClient.sendMail(email,"邮箱验证码",process);
            map.put("verifyCode",verifyCode);
            map.put("expirationTime", LocalDateTime.now().plusMinutes(5L));//过期时间
            return map;
        }
    }

    public Map<String,Object> resetPassword( String email,  String verifyCode, String password){
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)){
            map.put("emailMsg","请输入邮箱！");
        }else if(StringUtils.isBlank(verifyCode)){
            map.put("codeMsg","请输入验证码!");
        }else if (StringUtils.isBlank(password)){
            map.put("passwordMsg","请输入新密码！");
        }else{
            User user = userMapper.selectByEmail(email);
            userMapper.updatePassword(user.getId(),CommunityUtil.md5(password + user.getSalt()));
        }
        return map;
    }

}
