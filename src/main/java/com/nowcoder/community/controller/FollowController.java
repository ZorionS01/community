package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Hostholder;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Szw 2001
 * @Date 2023/6/20 18:22
 * @Slogn 致未来的你！
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostholder.getUser();
        followService.follow(user.getId(),entityType,entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_Follow)
                .setUserId(hostholder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);


        return CommunityUtil.getJSONString(0,"已关注");
    }

    @LoginRequired
    @RequestMapping(path = "/unFollow",method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType,int entityId){
        User user = hostholder.getUser();
        followService.unFollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJSONString(0,"已取消关注！");
    }

    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String,Object>> userList = followService.findFollowees(userId,page.getOffset(),page.getLimit());
        for (Map<String,Object> map : userList){
            User u = (User) map.get("user");
            map.put("hasFollowed",hasFollowed(u.getId()));
        }
        model.addAttribute("users",userList);
        return "/site/followee";
    }
    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int) followService.findFollowerCount(userId,ENTITY_TYPE_USER));

        List<Map<String,Object>> userList = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        for (Map<String,Object> map : userList){
            User u = (User) map.get("user");
            map.put("hasFollowed",hasFollowed(u.getId()));
        }
        model.addAttribute("users",userList);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId){
        if (hostholder.getUser() == null){
            return false;
        }

        return followService.hasFollowed(hostholder.getUser().getId(),
                ENTITY_TYPE_USER,userId);
    }
}
