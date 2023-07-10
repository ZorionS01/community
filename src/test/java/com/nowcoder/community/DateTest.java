package com.nowcoder.community;

import com.nowcoder.community.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/7/2 18:32
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class DateTest {

    @Test
    public void test1(){
        Date date = new Date();
        Date date1 = new Date(System.currentTimeMillis() + 5000);
        LocalDateTime localDateTime = DateUtil.dateToLocalDateTime(date);
        LocalDateTime localDateTime1 = DateUtil.dateToLocalDateTime(date1);
        System.out.println("localDateTime:"+localDateTime);
        LocalDate localDate = DateUtil.dateToLocalDate(date);
        System.out.println("localDate:"+localDate);
        LocalTime localTime = DateUtil.dateToLocalTime(date);
        System.out.println("localTime:" + localTime);
        System.out.println("时间比较:"+localDateTime.compareTo(localDateTime1));
    }
}
