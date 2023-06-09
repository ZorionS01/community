package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Hostholder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Author Szw 2001
 * @Date 2023/6/12 20:35
 * @Slogn 致未来的你！
 */
@RequestMapping("/discuss")
@Controller
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(DiscussPost discussPost){
        User user = hostholder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录哦！");
        }
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        //触发事件
        Event event = new Event()
        .setTopic(TOPIC_PUBLISH)
        .setUserId(user.getId())
        .setEntityType(ENTITY_TYPE_POST)
        .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);
        //计算帖子的分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,discussPost.getId());

        //报错的情况，将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model, Page page){
    //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
    //帖子
    DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
    model.addAttribute("post",post);
    //作者
    User user =  userService.findUserById(post.getUserId());
    model.addAttribute("user",user);
    //点赞数量
    long likeCount =  likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
    model.addAttribute("likeCount",likeCount);
    //点赞状态
    int likeStatus = hostholder.getUser() == null?0:
            likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_POST,discussPostId);
    model.addAttribute("likeStatus",likeStatus);

    //评论分页
    page.setLimit(10);
    page.setPath("/discuss/detail/"+discussPostId);
    page.setRows(post.getCommentCount());

    //评论：给帖子的评论
    //回复：给评论的评论
    //评论列表
    List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
    //评论VO列表
    List<Map<String,Object>> commentVoList = new ArrayList<>();
    if (commentList != null){
        for (Comment comment : commentList){
            //评论Vo
            Map<String,Object> commentVo = new HashMap<>();
            //评论
            commentVo.put("comment",comment);
            //作者
            commentVo.put("user",userService.findUserById(comment.getUserId()));
            //点赞数量
            likeCount =  likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
            commentVo.put("likeCount",likeCount);
            //点赞状态
            likeStatus = hostholder.getUser() == null?0:
                    likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_COMMENT,comment.getId());
            commentVo.put("likeStatus",likeStatus);
            //回复列表
            List<Comment> replyList = commentService.findCommentByEntity(
            ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
            //回复的Vo列表
            List<Map<String,Object>> replyVoList = new ArrayList<>();
            if (replyVoList != null){
                for (Comment reply : replyList){
                    Map<String,Object> replyVo = new HashMap<>();
                    //回复
                    replyVo.put("reply",reply);
                    //作者
                    replyVo.put("user",userService.findUserById(reply.getUserId()));
                    //回复目标
                    User target = reply.getTargetId() == 0? null:userService.findUserById(reply.getTargetId());
                    replyVo.put("target",target);
                    //点赞数量
                    likeCount =  likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                    replyVo.put("likeCount",likeCount);
                    //点赞状态
                    likeStatus = hostholder.getUser() == null?0:
                            likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_COMMENT,reply.getId());
                    replyVo.put("likeStatus",likeStatus);
                    replyVoList.add(replyVo);
                }
            }
            commentVo.put("replys",replyVoList);
            //回复数量
            int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
            commentVo.put("replyCount",replyCount);
            commentVoList.add(commentVo);
        }
    }
    model.addAttribute("comments",commentVoList);
    return "/site/discuss-detail";
    }

    //置顶、取消置顶
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);

        //获取置顶状态,1为置顶，0正常 1^1=1 1^0=0 异或
         int type = discussPost.getType()^1;
         discussPostService.updateType(id,type);

         //返回结果
         Map<String,Object> map = new HashMap<>();
         map.put("type",type);


        //触发事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostholder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,null,map);
    }

    //加精、取消加精
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);

        //获取状态,1为加精，0正常 1^1=1 0^1=0 异或运算
        int status = discussPost.getStatus()^1;
        discussPostService.updateStatus(id,status);
        //返回结果
        Map<String,Object> map = new HashMap<>();
        map.put("status",status);

        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);

        //触发事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostholder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,null,map);
    }

    //删除
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String delete(int id){
        discussPostService.updateStatus(id,2);

        //触发事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostholder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }
}
