package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 20:38
 * @Slogn 致未来的你！
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
