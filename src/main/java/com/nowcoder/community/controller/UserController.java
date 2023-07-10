package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Hostholder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author Szw 2001
 * @Date 2023/6/10 14:51
 * @Slogn 致未来的你！
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private FollowService followService;

    @Value("${qiniu.key.access}")
    public String accessKey;

    @Value("${qiniu.key.secret}")
    public String secretKey;

    @Value("${qiniu.bucket.header.name}")
    public String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    public String headerBucketUrl;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(Model model){
        //上传文件名称
        String fileName = CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName
        ,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        return "/site/setting";
    }

    //更新头像的路径
    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String uploadHeaderUrl(String fileName) throws InterruptedException {
        if (StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(1,"文件名不能为空!");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostholder.getUser().getId(),url);

        return CommunityUtil.getJSONString(0);
    }


    //废弃
//    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) throws InterruptedException {
        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        //更新当前用户的头像的路径(web访问路径)
        User user = hostholder.getUser();
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成随机文件名
        String filename = user.getUsername()+user.getId()+ suffix;
        File dest = new File(uploadPath+"/"+filename);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器异常"+e);
        }
        //http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //废弃
    /*@RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器的存放路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             OutputStream os  = response.getOutputStream();//mvc会自动管理 会自动关闭
             ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer))!= -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }*/

//    @LoginRequired
    @RequestMapping(path = "/password/update",method = RequestMethod.POST)
    public String updatePassword(@NonNull String oldPassword, @NonNull String newPassword, @NonNull String conPassword, Model model) throws InterruptedException {
        User user = hostholder.getUser();
        /*if (StringUtils.isBlank(oldPassword)||StringUtils.isBlank(newPassword)||StringUtils.isBlank(conPassword)){
            if (StringUtils.isBlank(oldPassword)){
                model.addAttribute("oldPassword","密码不能为空!");
            }

            if (StringUtils.isBlank(newPassword)){
                model.addAttribute("newPasswordMsg","新密码不能为空!");
            }
            if (StringUtils.isBlank(conPassword)){
                model.addAttribute("conPassword","密码不能为空!");
            }
            return "/site/setting";
        }*/
        String s = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(s)){
            model.addAttribute("oldPasswordMsg","密码与原密码不同！");
            return "/site/setting";
        }else if(!newPassword.equals(conPassword)){
            model.addAttribute("conPasswordMsg","俩次密码不一样!");
            return "/site/setting";
        }else {
            userService.updatePassword(user.getId(),CommunityUtil.md5(newPassword+user.getSalt()));
            return "redirect:/logout";
        }
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //查询关注数量
        long followeeCount =followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //查询粉丝的数量
        long followerCount = followService.findFollowerCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followerCount",followerCount);
        //查询是否关注
        boolean hasFollowed = false;
        if (hostholder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostholder.getUser().getId(),
                    ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
