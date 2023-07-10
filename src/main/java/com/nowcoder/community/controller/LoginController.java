package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Szw 2001
 * @Date 2023/5/30 18:59
 * @Slogn 致未来的你！
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    //http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model,@PathVariable("userId")int userId,@PathVariable("code")String code) throws InterruptedException {
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作,该账号已经激活过了!");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败,您提供的激活码不正确!");
            model.addAttribute("target","/register");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response /*HttpSession session*/){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

//        session.setAttribute("kaptcha",text);
        //验证码归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        //生效路径
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入redis
        String redisKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败:"+e.getMessage());
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String Login(String username, String password, String code , boolean rememberme,
                      Model model/*, HttpSession session*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner")String kaptchaOwner){
        //检查验证码
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNoneBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }

        //检查账号，密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //hashMap支持key,value为空 不能用get()获取value,可能为null表示 value值是null，也有可能表示可能没有该键
        if (map.containsKey("ticket")){
            //登录凭证存入数据库后，同时给前端传一份ticket以便以后进行身份验证 存入cookie中返回给客户端
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login"; //默认重定向是GET请求
    }

    @RequestMapping(path = "/sendVerifyCode",method = RequestMethod.GET)
    public String sendVerifyCode(@RequestParam("email") String email, Model model, HttpSession session){
        Map<String, Object> map = userService.sendVerifyCode(email);
        if (map.containsKey("emailMsg")){
            model.addAttribute("emailMsg",map.get("emailMsg"));
        }else {
            model.addAttribute("verifyCode",map.get("verifyCode"));
            session.setAttribute("verifyCode",map.get("verifyCode"));
            session.setAttribute("expirationTime",map.get("expirationTime"));
        }
        return "/site/forget";
    }


    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forget(String email,String code,String password,HttpSession session,Model model) throws InterruptedException {
        //判断验证码是否正确
        if (!code.equals(session.getAttribute("verifyCode"))){
            model.addAttribute("codeMsg","输入的验证码不正确！");
            return "/site/forget";
        } else if(LocalDateTime.now().isAfter((ChronoLocalDateTime<?>) session.getAttribute("expirationTime"))){
            //验证验证码是否过期
            model.addAttribute("codeMsg","输入的验证码已经过期,请重新点击获取验证码");
            return "/site/forget";
        }else {

            Map<String, Object> map = userService.resetPassword(email, code, password);
            if (map == null||map.isEmpty()){
                model.addAttribute("msg","密码修改成功可以使用新密码登录");
                model.addAttribute("target","/login");
                return "/site/operate-result";
            }else {
                model.addAttribute("emailMsg",map.get("emailMsg"));
                model.addAttribute("codeMsg",map.get("codeMsg"));
                model.addAttribute("passwordMsg",map.get("passwordMsg"));
                return "/site/forget";
            }
        }
    }
}
