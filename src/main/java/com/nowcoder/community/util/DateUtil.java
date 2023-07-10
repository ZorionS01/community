package com.nowcoder.community.util;

import java.time.*;
import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/7/2 18:22
 * @Slogn 致未来的你！
 */
public class DateUtil {

    //Date -> LocalDateTime
    public static LocalDateTime dateToLocalDateTime(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    //Date -> LocalDate
    public static LocalDate dateToLocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    //Date -> LocalTime
    public static LocalTime dateToLocalTime(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalTime localTime = instant.atZone(zoneId).toLocalTime();
        return localTime;
    }

    //LocalDateTime -> Date
    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    //LocalDate -> Date
    public static Date localDateToDate(LocalDate localDate){
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }


}
