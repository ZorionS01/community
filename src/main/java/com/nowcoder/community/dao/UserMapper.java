package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 13:15
 * @Slogn 致未来的你！
 */
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateUser(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);

    int updateStatus(int id,int status);

}
