package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @Author Szw 2001
 * @Date 2023/6/4 15:29
 * @Slogn 致未来的你！
 */
@Deprecated
public interface LoginTicketMapper {

    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
    "values(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id,user_id,ticket,status,expired ",
    "from login_ticket where ticket = #{ticket} "})
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket set status = #{status} where ticket = #{ticket} "})
    int updateStatus(String ticket,int status);

    @Delete({"delete from login_ticket where user_id = #{UserId}"})
    int deleteTicketByUserId(int UserId);

    @Select({"select count(id) from login_ticket where user_id = #{UserId} "})
    int selectTicketByUserId(int UserId);
}
