package com.nowcoder.community.service;

import com.nowcoder.community.util.DateUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author Szw 2001
 * @Date 2023/7/2 17:54
 * @Slogn 致未来的你！
 */
@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");




    //将指定IP计入UV
    public void recordUV(String IP){
        String dateTime = LocalDateTime.now().format(df);
        String redisKey = RedisKeyUtil.getUVKey(dateTime);
        redisTemplate.opsForHyperLogLog().add(redisKey,IP);

    }

    //统计指定日期范围内的UV
    public long calculateUV(Date startDate,Date endDate){
        if (startDate == null || endDate == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //整理日期范围内的key
        List<String> keyList = new ArrayList<>();
        LocalDateTime localDateTime = DateUtil.dateToLocalDateTime(startDate);
        LocalDateTime localDateTime1 = DateUtil.dateToLocalDateTime(endDate);
        while (localDateTime.compareTo(localDateTime1) <= 0){
            String key = RedisKeyUtil.getUVKey(localDateTime.format(df));
            keyList.add(key);
            localDateTime = localDateTime.plusDays(1);
        }


        //合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(localDateTime.format(df),localDateTime1.format(df));

        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将指定用户计入DAU
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(DateUtil.dateToLocalDateTime(new Date()).format(df));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    //统计指定日期范围内的DAU
    public long calculateDAU(Date startDate,Date endDate){
        if (startDate == null || endDate == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //整理日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        LocalDateTime localDateTime = DateUtil.dateToLocalDateTime(startDate);
        LocalDateTime localDateTime1 = DateUtil.dateToLocalDateTime(endDate);
        while (localDateTime.compareTo(localDateTime1) <= 0){
            String key = RedisKeyUtil.getDAUKey(localDateTime.format(df));
            keyList.add(key.getBytes());
            localDateTime = localDateTime.plusDays(1);
        }


        //进行or运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(DateUtil.dateToLocalDateTime(startDate).format(df),DateUtil.dateToLocalDateTime(endDate).format(df));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
