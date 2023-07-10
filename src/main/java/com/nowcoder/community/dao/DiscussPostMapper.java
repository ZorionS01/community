package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 17:03
 * @Slogn 致未来的你！
 */
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,@Param("offset") int offset,
                                         @Param("limit") int limit,@Param("orderMode") int orderMode);

    //@Param注解用于给参数取别名 动态sql一定要加该注解
    //如果只有一个参数，并且在<if>里使用，则必须加该参数取别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

    int updateType(int id,int type);

    int updateStatus(int id,int status);

    int updateScore(int id,double score);


}
