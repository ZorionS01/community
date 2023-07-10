package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.DateUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/7/6 18:31
 * @Slogn 致未来的你！
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {



    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticService elasticService;

    //牛客纪元
    private static final LocalDateTime epoch;

    static {
        epoch = LocalDateTime.of(2023,7,1,0,0,0);
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0){
            logger.info("[任务取消] 没有需要刷新的帖子");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数："+operations.size());
        while (operations.size() > 0){
            //redis中set类型pop随机获取一个数据并移除
            this.refresh((Integer)operations.pop());
        }
        logger.info("[任务结束] 帖子刷新完毕");
    }

    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null){
            logger.error("该帖子不存在:id = "+postId);
        }

        //是否精华
        boolean wonderful = post.getStatus() == 1;
        //评论数量
        int commentCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);

        //计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //分数 = 帖子的权重 + 距离天数
        /*double score = Math.log10(Math.max(w,1)) +
                (DateUtil.dateToLocalDateTime(post.getCreateTime()).toEpochSecond(ZoneOffset.of("+8"))
                - epoch.toEpochSecond(ZoneOffset.of("+8"))/(1000*3600*24));*/
        double score = Math.log10(Math.max(w,1))+
                (post.getCreateTime().getTime() - epoch.toEpochSecond(ZoneOffset.of("+8"))/(1000*3600*24));
        //更新帖子分数
        discussPostService.updateScore(postId,score);
        //同步搜索数据
        post.setScore(score);
        elasticService.saveDiscussPost(post);
    }
}
