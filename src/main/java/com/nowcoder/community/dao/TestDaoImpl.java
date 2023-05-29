package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @Author Szw 2001
 * @Date 2023/5/27 18:32
 * @Slogn 致未来的你！
 */
@Repository
public class TestDaoImpl implements TestDao{
    @Override
    public String select() {
        return "Test1";
    }
}
